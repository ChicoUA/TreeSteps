package pt.ua.icm.treesteps;

public class User {
    private String email;
    private String name;
    private int age;
    private String country;
    private int creds;

    public User() {
        this.email = "";
        this.name = "";
        this.age = 0;
        this.country = "";
        this.creds = 0;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getCreds() {
        return creds;
    }

    public void setCreds(int creds) {
        this.creds = creds;
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", country='" + country + '\'' +
                ", creds=" + creds +
                '}';
    }
}
