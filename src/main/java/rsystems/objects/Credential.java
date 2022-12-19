package rsystems.objects;

public class Credential {

    private final String access_Token;
    private final String refresh_Token;

    public Credential(String access_Token, String refresh_Token) {
        this.access_Token = access_Token;
        this.refresh_Token = refresh_Token;
    }

    public String getAccess_Token() {
        return access_Token;
    }

    public String getRefresh_Token() {
        return refresh_Token;
    }
}
