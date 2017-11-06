package ratpack.testSmartApp;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import ratpack.guice.Guice;
import ratpack.handling.Chain;
import ratpack.http.client.ReceivedResponse;
import ratpack.server.BaseDir;
import ratpack.smartapp.GetHandler;
import ratpack.smartapp.MyModule;
import ratpack.smartapp.PostHandler;
import ratpack.test.embed.EmbeddedApp;

@RunWith(JUnit4.class)
public class SmartAppTest {

	 @Test
	  public void completeTest() throws Exception {
		 
		 // Embedded server Starts and automatically ends when for each loop stops its execution 
		  EmbeddedApp.of(s-> s
				  .serverConfig(c -> c.baseDir(BaseDir.find()))
			        .registry(Guice.registry(b -> b.module(MyModule.class)))
			        .handlers(chain -> chain
			        		.get("switchStatus",GetHandler.class)
			            	.post("setLevel", PostHandler.class)
			            	// Bind the /static app path to the src/ratpack/assets/images dir
			                .prefix("static", nested -> nested.fileSystem("assets/images", Chain::files)) 
			                .all(ctx -> ctx.render("Invalid Path"))
			        )  
		).test(httpClient -> {
					String [] lightValues = {"0","25","50","99"};
					for (String value : lightValues) {
						ReceivedResponse postResponse = httpClient.requestSpec(s ->
				         s.body(b -> b.type("application/json").text("{\"value\":\""+value+"\"}"))
				        ).post("setLevel");
						
				        assertEquals("Value Successfully set to Dimmers", postResponse.getBody().getText());
				      
				        ReceivedResponse getResponse = httpClient.get("switchStatus");
				        assertEquals("text/plain;charset=UTF-8", getResponse.getHeaders().get("Content-Type"));
				        String expectedString = "[{\"name\":\"DimmerSwitch\",\"level\":"+value+"}]";
				        System.out.println("Checking the response contains "+value+" or not in switches get request - "+getResponse.getBody().getText());
				        assertEquals(expectedString, getResponse.getBody().getText());
				        
					}
		});
	  }
}
