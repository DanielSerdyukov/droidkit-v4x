package droidkit.sqlite;

import android.support.annotation.NonNull;

/**
 * @author Daniel Serdyukov
 */
public class MockUser {

    String mName;

    int mAge;

    public long getRowId() {
        throw new UnsupportedOperationException();
    }

    public String getName() {
        return mName;
    }

    public void setName(@NonNull String name) {
        mName = name;
    }

    public int getAge() {
        return mAge;
    }

    public void setAge(int age) {
        mAge = age;
    }

}
