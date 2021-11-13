package rsystems.objects;

import java.sql.Timestamp;

public class KarmaUser {

    private Long id;
    private int karmaAmount;
    private Timestamp lastKarmaReceived;
    private Timestamp lastPointReceived;
    private int positiveKarmaSent;
    private int negativeKarmaSent;
    private int earnedKarmaRank;

}
