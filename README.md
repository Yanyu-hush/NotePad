README 文档：笔记应用功能概述


功能
1. 笔记管理
   创建笔记：用户可以创建新的笔记，输入标题和内容。
   查看笔记：用户可以浏览所有笔记的列表，点击任意笔记查看详细内容。
   编辑笔记：用户可以选择任意笔记进行编辑，修改其标题和内容。
   删除笔记：用户可以从列表中删除不需要的笔记。
2. 搜索功能
   按内容搜索：用户可以输入关键词，应用将搜索笔记内容中包含该关键词的所有笔记，并显示在搜索结果列表中。 
       @Override
       public boolean onCreateOptionsMenu(Menu menu) {
   
        // Inflate menu from XML resource
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_options_menu, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default

        // Optional: set a listener for search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterNotesByQuery(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterNotesByQuery(newText);
                return true;
            }
        });

           return super.onCreateOptionsMenu(menu);
       }
       private void filterNotesByQuery(String query) {
       if (query == null || query.isEmpty()) {
       // Show all notes
       showAllNotes();
       } else {
       // Query the database for notes containing the search query
       Cursor cursor = getContentResolver().query(
       NotePad.Notes.CONTENT_URI,
       PROJECTION,
       NotePad.Notes.COLUMN_NAME_TITLE + " LIKE ?",
       new String[]{"%" + query + "%"},
       NotePad.Notes.DEFAULT_SORT_ORDER
       );

            // Update the adapter with the filtered cursor
            SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                    this,
                    R.layout.noteslist_item,
                    cursor,
                    new String[]{NotePad.Notes.COLUMN_NAME_TITLE, NotePad.Notes.COLUMN_NAME_CREATE_DATE,    NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE},
                    new int[]{android.R.id.text1, R.id.text2, R.id.text3},
                    0
               ) {
                @Override
                public void bindView(View view, Context context, Cursor cursor) {
                    super.bindView(view, context, cursor);

                    // 获取时间戳
                    long createDate = cursor.getLong(COLUMN_INDEX_CREATE_DATE);
                    long modificationDate = cursor.getLong(COLUMN_INDEX_MODIFICATION_DATE);

                    // 格式化时间戳
                    String formattedCreateDate = dateFormat.format(new Date(createDate));
                    String formattedModificationDate = dateFormat.format(new Date(modificationDate));

                    // 设置格式化后的日期到对应的TextView
                    TextView tvCreateDate = (TextView) view.findViewById(R.id.text2);
                    TextView tvModificationDate = (TextView) view.findViewById(R.id.text3);
                    tvCreateDate.setText(formattedCreateDate);
                    tvModificationDate.setText(formattedModificationDate);
                }
            };

            setListAdapter(adapter);
        }
    }

    private void showAllNotes() {
    Cursor cursor = managedQuery(
    getIntent().getData(),
    PROJECTION,
    null,
    null,
    NotePad.Notes.DEFAULT_SORT_ORDER
    );

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                this,
                R.layout.noteslist_item,
                cursor,
                new String[]{NotePad.Notes.COLUMN_NAME_TITLE, NotePad.Notes.COLUMN_NAME_CREATE_DATE, NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE},
                new int[]{android.R.id.text1, R.id.text2, R.id.text3},
                0
        ) {
            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                super.bindView(view, context, cursor);

                // 获取时间戳
                long createDate = cursor.getLong(COLUMN_INDEX_CREATE_DATE);
                long modificationDate = cursor.getLong(COLUMN_INDEX_MODIFICATION_DATE);

                // 格式化时间戳
                String formattedCreateDate = dateFormat.format(new Date(createDate));
                String formattedModificationDate = dateFormat.format(new Date(modificationDate));

                // 设置格式化后的日期到对应的TextView
                TextView tvCreateDate = (TextView) view.findViewById(R.id.text2);
                TextView tvModificationDate = (TextView) view.findViewById(R.id.text3);
                tvCreateDate.setText(formattedCreateDate);
                tvModificationDate.setText(formattedModificationDate);
            }
        };

        setListAdapter(adapter);
    }
  ![image](https://github.com/user-attachments/assets/17b1ccec-d158-4484-93b0-adeff24947bd)
![image](https://github.com/user-attachments/assets/98254b81-455e-4061-80be-9f071a0af3d7)

可以按内容搜索出对应的note

4. 时间戳显示
   每条笔记都显示两个时间戳：
   创建时间戳：记录笔记创建的时间。
   修改时间戳：记录笔记最后被修改的时间。
   private static final String[] PROJECTION = new String[] {
   NotePad.Notes._ID, // 0
   NotePad.Notes.COLUMN_NAME_TITLE, // 1
   NotePad.Notes.COLUMN_NAME_CREATE_DATE, // 2
   NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE, // 3
   };

   /** The index of the title column */
   private static final int COLUMN_INDEX_TITLE = 1;
   private static final int COLUMN_INDEX_CREATE_DATE = 2;
   private static final int COLUMN_INDEX_MODIFICATION_DATE = 3;

   /**
    * onCreate is called when Android starts this Activity from scratch.
      */
      @Override
      protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      // The user does not need to hold down the key to use menu shortcuts.
      setDefaultKeyMode(DEFAULT_KEYS_SHORTCUT);

      /* If no data is given in the Intent that started this Activity, then this Activity
        * was started when the intent filter matched a MAIN action. We should use the default
        * provider URI.
          */
          // Gets the intent that started this Activity.
          Intent intent = getIntent();

      // If there is no data associated with the Intent, sets the data to the default URI, which
      // accesses a list of notes.
      if (intent.getData() == null) {
      intent.setData(NotePad.Notes.CONTENT_URI);
      }

      /*
        * Sets the callback for context menu activation for the ListView. The listener is set
        * to be this Activity. The effect is that context menus are enabled for items in the
        * ListView, and the context menu is handled by a method in NotesList.
          */
          getListView().setOnCreateContextMenuListener(this);

      /* Performs a managed query. The Activity handles closing and requerying the cursor
        * when needed.
        *
        * Please see the introductory note about performing provider operations on the UI thread.
          */
          Cursor cursor = managedQuery(
          getIntent().getData(),            // Use the default content URI for the provider.
          PROJECTION,                       // Return the note ID and title for each note.
          null,                             // No where clause, return all records.
          null,                             // No where clause, therefore no where column values.
          NotePad.Notes.DEFAULT_SORT_ORDER  // Use the default sort order.
          );

      /*
        * The following two arrays create a "map" between columns in the cursor and view IDs
        * for items in the ListView. Each element in the dataColumns array represents
        * a column name; each element in the viewID array represents the ID of a View.
        * The SimpleCursorAdapter maps them in ascending order to determine where each column
        * value will appear in the ListView.
          */

      // The names of the cursor columns to display in the view, initialized to the title column
      String[] dataColumns = {
      NotePad.Notes.COLUMN_NAME_TITLE,
      NotePad.Notes.COLUMN_NAME_CREATE_DATE,
      NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE
      };


        // The view IDs that will display the cursor columns, initialized to the TextView in
        // noteslist_item.xml
        int[] viewIDs = {
                android.R.id.text1,
                R.id.text2, // 布局中添加了一个TextView来显示创建时间
                R.id.text3 // 在布局中添加了一个TextView来显示修改时间
        };

        // Creates the backing adapter for the ListView.
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                this,
                R.layout.noteslist_item,
                cursor,
                new String[]{NotePad.Notes.COLUMN_NAME_TITLE, NotePad.Notes.COLUMN_NAME_CREATE_DATE, NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE},
                new int[]{android.R.id.text1, R.id.text2, R.id.text3},
                0
        ) {
            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                super.bindView(view, context, cursor);
                // 获取标题
                String title = cursor.getString(COLUMN_INDEX_TITLE);
                // 获取时间戳
                long createDate = cursor.getLong(COLUMN_INDEX_CREATE_DATE);
                long modificationDate = cursor.getLong(COLUMN_INDEX_MODIFICATION_DATE);

                // 格式化时间戳
                String formattedCreateDate = dateFormat.format(new Date(createDate));
                String formattedModificationDate = dateFormat.format(new Date(modificationDate));

                // 设置标题到对应的TextView
                TextView tvTitle = (TextView) view.findViewById(R.id.text1);
                tvTitle.setText(title);
                TextView tvCreateDate = (TextView) view.findViewById(R.id.text2);
                TextView tvModificationDate = (TextView) view.findViewById(R.id.text3);
                tvCreateDate.setText(formattedCreateDate);
                tvModificationDate.setText(formattedModificationDate);
            }
        };

        // Sets the ListView's adapter to be the cursor adapter that was just created.
        setListAdapter(adapter);
   ![image](https://github.com/user-attachments/assets/44cdf314-2308-4a4e-9800-980c736a6113)

xml文件如下<?xml version="1.0" encoding="utf-8"?>
<menu xmlns:android="http://schemas.android.com/apk/res/android">
    <!--  This is our one standard application action (creating a new note). -->
    <item android:id="@+id/menu_add"
          android:icon="@drawable/ic_menu_compose"
          android:title="@string/menu_add"
          android:alphabeticShortcut='a'
          android:showAsAction="always" />
    <item
        android:id="@+id/action_sort_by_date"
        android:title="Sort by Date"
        android:orderInCategory="100"
        android:showAsAction="never" />
    <item
        android:id="@+id/action_change_color"
        android:title="Change Background Color"
        android:orderInCategory="100"
        android:showAsAction="never" />
    <item
        android:id="@+id/menu_search"
        android:icon="@drawable/serach"
        android:title="@string/menu_search"
        android:alphabeticShortcut="s"
        android:showAsAction="ifRoom|collapseActionView"
        android:actionViewClass="android.widget.SearchView" />
    <!--  If there is currently data in the clipboard, this adds a PASTE menu item to the menu
          so that the user can paste in the data.. -->
    <item android:id="@+id/menu_paste"
          android:icon="@drawable/ic_menu_compose"
          android:title="@string/menu_paste"
          android:alphabeticShortcut='p' />
</menu>
   技术栈
   编程语言：Java
   开发环境：Android Studio
   数据库：SQLite
   用户界面：基于 Android XML 的布局和视图

4. 拓展功能
   ![image](https://github.com/user-attachments/assets/ae8fa345-4552-4cf1-a6c7-369cff4ebd89)

   1：
### 更改背景颜色
此功能允许用户通过在选项菜单中选择一个选项来随机更改应用的背景颜色。
![image](https://github.com/user-attachments/assets/2cb129e1-50d8-4d96-9f99-b989a4247984)

### 按日期排序笔记
用户可以使用选项菜单按日期排序他们的笔记。此功能允许根据笔记的创建或修改日期以降序显示笔记。
![image](https://github.com/user-attachments/assets/35dbe5c4-3235-4651-aec2-6361aca03ce7)

代码概况：
<item
android:id="@+id/action_sort_by_date"
android:title="Sort by Date"
android:orderInCategory="100"
android:showAsAction="never" />
<item
android:id="@+id/action_change_color"
android:title="Change Background Color"
android:orderInCategory="100"
android:showAsAction="never" />
@Override
public boolean onOptionsItemSelected(MenuItem item) {
int id = item.getItemId();
if (id==R.id.menu_add){
startActivity(new Intent(Intent.ACTION_INSERT, getIntent().getData()));
return true;
}
if(id==R.id.menu_paste){
startActivity(new Intent(Intent.ACTION_PASTE, getIntent().getData()));
return true;
}
if (id == R.id.action_change_color) {
changeBackgroundColor();
return true;
}
if (id == R.id.action_sort_by_date) {
sortNotesByDate();
return true;
}

        return super.onOptionsItemSelected(item);
    }

    private void changeBackgroundColor() {
        // 随机生成一个颜色
        int randomColor = Color.argb(255, new Random().nextInt(256), new Random().nextInt(256), new Random().nextInt(256));
        // 设置背景颜色
        findViewById(android.R.id.content).setBackgroundColor(randomColor);
    }
    private void sortNotesByDate() {
        // 这里需要根据时间排序你的笔记
        // 你可能需要重新查询数据库，并更新ListView
        Cursor cursor = managedQuery(
                NotePad.Notes.CONTENT_URI,
                PROJECTION,
                null,
                null,
                NotePad.Notes.COLUMN_NAME_CREATE_DATE + " DESC" // 假设你想要按照创建时间降序排序
        );

        // 更新你的ListView的Adapter
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                this,
                R.layout.noteslist_item,
                cursor,
                new String[]{NotePad.Notes.COLUMN_NAME_TITLE, NotePad.Notes.COLUMN_NAME_CREATE_DATE, NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE},
                new int[]{android.R.id.text1, R.id.text2, R.id.text3},
                0
        ) {
            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                super.bindView(view, context, cursor);

                // 获取时间戳
                long createDate = cursor.getLong(COLUMN_INDEX_CREATE_DATE);
                long modificationDate = cursor.getLong(COLUMN_INDEX_MODIFICATION_DATE);

                // 格式化时间戳
                String formattedCreateDate = dateFormat.format(new Date(createDate));
                String formattedModificationDate = dateFormat.format(new Date(modificationDate));

                // 设置格式化后的日期到对应的TextView
                TextView tvCreateDate = (TextView) view.findViewById(R.id.text2);
                TextView tvModificationDate = (TextView) view.findViewById(R.id.text3);
                tvCreateDate.setText(formattedCreateDate);
                tvModificationDate.setText(formattedModificationDate);
            }
        };

        setListAdapter(adapter);
    }
   使用说明
   打开应用后，点击 "Add Note" 按钮创建新笔记。
   在笔记列表中，点击任意笔记查看详情。
   点击笔记详情页面的 "Edit" 按钮编辑笔记。
   使用搜索框输入关键词搜索相关笔记。
   optionmenu中添加两个选择可以改变背景颜色和按时间排序note
