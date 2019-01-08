package com.zjm.zviews;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.zjm.zviewlibrary.recycleryView.NestedRecycleryView;

/**
 * 支持嵌套滑动的 Recyclerview，把这个 view 放进滑动的父控件(ScrollView RecyclerView 等等)内部，当 Recyclerview 滑动到底部或者顶部时，自动滑动父控件
 * <p>
 * When I wrote this, only God and I understood what I was doing
 * Now, God only knows
 *
 * @author zjm
 * @date 2018/12/4
 */
public class NestedRecycleryActivity extends AppCompatActivity {

    private NestedRecycleryView mRecycleryView;
    private Switch mSwitchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nested_recyclery);

        mSwitchView = findViewById(R.id.switchView);
        mRecycleryView = findViewById(R.id.recycleryView);
        initView();
    }

    private void initView() {
        mRecycleryView.setLayoutManager(new LinearLayoutManager(this));
        TestAdapter testAdapter = new TestAdapter(this);
        mRecycleryView.setAdapter(testAdapter);
        mRecycleryView.setNestedScrollingEnabled(false);

        mSwitchView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mRecycleryView.setInterceptTouchEvent(isChecked);
            }
        });
    }


    /**
     * 测试随便编的 adapter
     */
    public class TestAdapter extends RecyclerView.Adapter<TestAdapter.ViewHodle> {
        private Context mContext;

        public TestAdapter(Context context) {
            mContext = context;
        }

        @Override
        public ViewHodle onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHodle(new TextView(mContext));
        }

        @Override
        public void onBindViewHolder(ViewHodle holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 30;
        }

        public class ViewHodle extends RecyclerView.ViewHolder {

            public ViewHodle(View itemView) {
                super(itemView);
                TextView textView = (TextView) itemView;
                textView.setText("测试测试测试测试测试测试");
                textView.setTextSize(20);
            }
        }
    }
}
