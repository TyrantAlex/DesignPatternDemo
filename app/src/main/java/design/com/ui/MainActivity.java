package design.com.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import design.com.java.build.BuilderBean;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.tv_build)
    TextView tvBuild;
    @BindView(R.id.btn_build)
    Button btnBuild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initListener();
    }

    private void initListener() {
        btnBuild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initData();
            }
        });
    }

    private void initData() {
        BuilderBean steamBuyBuilder = new BuilderBean.Builder()
                .buyBloodBorne()
//                .buyCivilizationSix()
//                .buyDarkSoulThree()
                .buyWatchDogsTwo()
                .build();
        tvBuild.setText("玩家已购买: " + steamBuyBuilder);
    }
}
