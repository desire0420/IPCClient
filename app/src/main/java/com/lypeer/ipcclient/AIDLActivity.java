package com.lypeer.ipcclient;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.lypeer.ipcclient.Book;

import java.util.List;

/**
 * 客户端的AIDLActivity.java
 * 由于测试机的无用debug信息太多，故log都是用的e
 * <p>
 * Created by lypeer on 2016/7/17.
 */
public class AIDLActivity extends Activity {
    private Button startmain;
    private Button addBookIn, addBookOut, addBookInout;

    //由AIDL文件生成的Java类
    private BookManager mBookManager = null;

    //标志当前与服务端连接状况的布尔值，false为未连接，true为连接中
    private boolean mBound = false;

    //包含Book对象的list
    private List<Book> mBooks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aidl);
        startmain = (Button) findViewById(R.id.startmain);
        addBookIn = (Button) findViewById(R.id.addBookIn);
        startmain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AIDLActivity.this, MainActivity.class));
            }
        });

        /**
         * 按钮的点击事件，点击之后调用服务端的addBookIn方法
         *
         * @param view
         */
        addBookIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //如果与服务端的连接处于未连接状态，则尝试连接
                if (!mBound) {
                    attemptToBindService();
                    Toast.makeText(AIDLActivity.this, "当前与服务端处于未连接状态，正在尝试重连，请稍后再试", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mBookManager == null) return;

                Book book = new Book();
                book.setName("APP研发录In");
                book.setPrice(30);
                try {
                    //获得服务端执行方法的返回值，并打印输出
                    Book returnBook = mBookManager.addBookIn(book);
                    Log.e(getLocalClassName(), "-----" + returnBook.toString());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 尝试与服务端建立连接
     */
    private void attemptToBindService() {
        Intent intent = new Intent();
        intent.setAction("com.lypeer.aidl");
        intent.setPackage("com.lypeer.ipcserver");
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mBound) {
            attemptToBindService();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mBound) {
            unbindService(mServiceConnection);
            mBound = false;
        }
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e(getLocalClassName(), "service connected");
            mBookManager = BookManager.Stub.asInterface(service);
            mBound = true;

            if (mBookManager != null) {
                try {
                    mBooks = mBookManager.getBooks();
                    Log.e(getLocalClassName(),"onServiceConnected--"+ mBooks.toString());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e(getLocalClassName(), "service disconnected");
            mBound = false;
        }
    };
}
