package example.documenttype;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import design.com.ui.R;

public class DocumentTypeTestActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_document_type);
        findViewById(R.id.tv_show);
    }
}
