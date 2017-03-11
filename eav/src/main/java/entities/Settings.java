package entities;

/**
 * Created by Lawrence on 11.03.2017.
 */
public class Settings extends BaseEntitie {

    private Integer id;

    private Integer user_id; //401

    private String emailNewMessage;  //402

    private String emailNewFriend;  //403

    private String emailMeetingInvite;  //404

    private String phoneNewMessage; //405

    private String phoneNewFriend;  //406

    private String phoneMeetingInvite;  //407

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public String getEmailNewMessage() {
        return emailNewMessage;
    }

    public void setEmailNewMessage(String emailNewMessage) {
        this.emailNewMessage = emailNewMessage;
    }

    public String getEmailNewFriend() {
        return emailNewFriend;
    }

    public void setEmailNewFriend(String emailNewFriend) {
        this.emailNewFriend = emailNewFriend;
    }

    public String getEmailMeetingInvite() {
        return emailMeetingInvite;
    }

    public void setEmailMeetingInvite(String emailMeetingInvite) {
        this.emailMeetingInvite = emailMeetingInvite;
    }

    public String getPhoneNewMessage() {
        return phoneNewMessage;
    }

    public void setPhoneNewMessage(String phoneNewMessage) {
        this.phoneNewMessage = phoneNewMessage;
    }

    public String getPhoneNewFriend() {
        return phoneNewFriend;
    }

    public void setPhoneNewFriend(String phoneNewFriend) {
        this.phoneNewFriend = phoneNewFriend;
    }

    public String getPhoneMeetingInvite() {
        return phoneMeetingInvite;
    }

    public void setPhoneMeetingInvite(String phoneMeetingInvite) {
        this.phoneMeetingInvite = phoneMeetingInvite;
    }

    public Settings(Integer id, Integer user_id) {
        this.id = id;
        this.user_id = user_id;
        this.emailNewMessage = "false";
        this.emailNewFriend = "false";
        this.emailMeetingInvite = "false";
        this.phoneNewMessage = "false";
        this.phoneNewFriend = "false";
        this.phoneMeetingInvite = "false";
    }
}
