package ng.com.grazac.jasperadmin;

public class UserData {
    private int id;
    private String phone;
    private String data;

    public UserData(int id, String phone, String data) {
        this.id = id;
        this.phone = phone;
        this.data = data;
    }

    public UserData() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
