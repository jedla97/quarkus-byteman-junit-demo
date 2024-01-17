package quarkus.qe.byteman;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("")
public class GreetingResource {

    public boolean modify = true;
    public boolean modified = false;

    @GET
    @Path("modifyVariable")
    @Produces(MediaType.TEXT_PLAIN)
    public String modifyVariable() {
        String response = "Hello from RESTEasy Reactive";
        return response;
    }

    @GET
    @Path("readBindModify")
    @Produces(MediaType.TEXT_PLAIN)
    public String readBindModify() {
        return createString(modify);
    }

    @GET
    @Path("readVariableThrowError")
    @Produces(MediaType.TEXT_PLAIN)
    public String readVariableThrowError() {
        try {
            helperMethod();
        } catch (RuntimeException e) {
            return e.getMessage();
        }
        return "Exception from byteman wasn't thrown";
    }

    public void helperMethod(){
        // Some complex method which can throw some exception
        // Used to show it can be intercepted by byteman
    }

    public String createString(boolean modify) {
        modified = false;
        String response1 = "Hello from ";
        String response2 = "RESTEasy Reactive";
        this.modify = !modify;
        return response1 + response2 + ". Byteman modified boolean value = " + modified;
    }
}
