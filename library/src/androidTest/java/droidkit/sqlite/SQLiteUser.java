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
    long mId;

    @SQLiteColumn("name")
    String mName;

    @SQLiteColumn("age")
    int mAge;

    @SQLiteColumn("balance")
    double mBalance;

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

    @Override
    public String toString() {
        return "SQLiteUser{" +
                "mId=" + mId +
                ", mName='" + mName + '\'' +
                ", mAge=" + mAge +
                ", mBalance=" + mBalance +
                '}';
    }

}
