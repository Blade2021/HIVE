package rsystems;

public class UserModel {

    long userid;
    String HookTrigger;


    /*
    public UserModel(long inputUserId, String inputHookTrigger){
        this.userid = inputUserId;
        this.HookTrigger = inputHookTrigger;
    }
    */
    public long getUserid() {
        return userid;
    }

    public void setUserid(long userid) {
        this.userid = userid;
    }

    public String getHookTrigger() {
        return HookTrigger;
    }

    public void setHookTrigger(String hookTrigger) {
        HookTrigger = hookTrigger;
    }

}
