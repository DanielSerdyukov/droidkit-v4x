package droidkit.sqlite;

import android.net.Uri;
import android.provider.BaseColumns;

import droidkit.BuildConfig;
import droidkit.annotation.SQLiteColumn;
import droidkit.annotation.SQLiteObject;
import droidkit.annotation.SQLitePk;

/**
 * @author Daniel Serdyukov
 */
@SQLiteObject("users")
public class User {

    public static final Uri URI = new Uri.Builder()
            .scheme(SQLiteProvider.SCHEME)
            .authority(BuildConfig.APPLICATION_ID)
            .appendPath("users")
            .build();

    @SQLitePk
    private long mId;

    @SQLiteColumn(Columns.NAME)
    private String mName;

    @SQLiteColumn(Columns.AGE)
    private int mAge;

    @SQLiteColumn(Columns.BALANCE)
    private double mBalance;

    @SQLiteColumn(Columns.BLOCKED)
    private boolean mBlocked;

    @SQLiteColumn(Columns.AVATAR)
    private byte[] mAvatar;

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

    public static interface Columns extends BaseColumns {
        String NAME = "name";
        String AGE = "age";
        String BALANCE = "balance";
        String BLOCKED = "blocked";
        String AVATAR = "avatar";
    }

}
