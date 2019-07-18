package com.byted.camp.todolist;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.byted.camp.todolist.beans.State;
import com.byted.camp.todolist.db.TodoContract;
import com.byted.camp.todolist.db.TodoDbHelper;

public class NoteActivity extends AppCompatActivity {

    private EditText editText;
    private Button addBtn;
    private RadioGroup importantGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        setTitle(R.string.take_a_note);

        importantGroup = findViewById(R.id.rg_priorities);

        editText = findViewById(R.id.edit_text);
        editText.setFocusable(true);
        editText.requestFocus();
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null) {
            inputManager.showSoftInput(editText, 0);
        }

        addBtn = findViewById(R.id.btn_add);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence content = editText.getText();
                if (TextUtils.isEmpty(content)) {
                    Toast.makeText(NoteActivity.this,
                            "No content to add", Toast.LENGTH_SHORT).show();
                    return;
                }
                int priority = 0;
                switch (importantGroup.getCheckedRadioButtonId()){
                    case R.id.rb_mi:
                        priority = 3;
                        break;
                    case R.id.rb_ni:
                        priority = 2;
                        break;
                    case R.id.rb_li:
                        priority = 1;
                        break;
                }
                boolean succeed = saveNote2Database(content.toString().trim(),priority);
                if (succeed) {
                    Toast.makeText(NoteActivity.this,
                            "Note added", Toast.LENGTH_SHORT).show();
                    setResult(Activity.RESULT_OK);
                } else {
                    Toast.makeText(NoteActivity.this,
                            "Error", Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private boolean saveNote2Database(String content,int priority) {

        // TODO 插入一条新数据，返回是否插入成功
        TodoDbHelper todoDbHelper = new TodoDbHelper(getApplicationContext());

        SQLiteDatabase db = todoDbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(TodoContract.TodoEntry.CONTENT,content);
        contentValues.put(TodoContract.TodoEntry.DATE,System.currentTimeMillis());
        contentValues.put(TodoContract.TodoEntry.STATE, State.TODO.intValue);
        contentValues.put(TodoContract.TodoEntry.PRIORITY,priority);

        long newRowId = db.insert(TodoContract.TodoEntry.TABLE_NAME,null,contentValues);
        return newRowId != -1;
    }
}
