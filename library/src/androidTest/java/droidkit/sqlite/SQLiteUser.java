package droidkit.sqlite;

import android.net.Uri;

import droidkit.annotation.SQLiteColumn;
import droidkit.annotation.SQLiteObject;
import droidkit.annotation.SQLitePk;
import droidkit.test.BuildConfig;

/**
 * @author Daniel Serdyukov
 */
@SQLiteObject("users")
public class SQLiteUser {

    public static final Uri URI = new Uri.Builder()
            .scheme(SQLiteProvider.SCHEME)
            .authority(BuildConfig.APPLICATION_ID)
            .appendPath("users")
            .build();

    @SQLitePk
    private long mId;

    @SQLiteColumn("name")
    private String mName;

    @SQLiteColumn("age")
    private int mAge;

    @SQLiteColumn("balance")
    private double mBalance;

    @SQLiteColumn("blocked")
    private boolean mBlocked;

    public long getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public int getAge() {
        return mAge;
    }

    public void setAge(int age) {
        this.mAge = age;
    }

    public double getBalance() {
        return mBalance;
    }

    public void setBalance(double balance) {
        mBalance = balance;
    }

    public boolean isBlocked() {
        return mBlocked;
    }

    public void setBlocked(boolean blocked) {
        mBlocked = blocked;
    }

}
