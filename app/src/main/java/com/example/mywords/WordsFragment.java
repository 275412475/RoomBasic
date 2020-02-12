package com.example.mywords;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class WordsFragment extends Fragment {
    private WordViewMudel viewMudel;
    private RecyclerView recyclerView;
    private MyAdapter adapter1,adapter2;
    private LiveData<List<Word>> filteredWords;
    private List<Word> allWords;
    private static final String VIEW_TYPE_SHP  = "view_type_shp";
    private static final String IS_USING_CARDVIEW  = "is_using_cardview";
    private  boolean undo;
    private DividerItemDecoration dividerItemDecoration;
    public WordsFragment() {
        // Required empty public constructor
        setHasOptionsMenu(true);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_words, container, false);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewMudel = ViewModelProviders.of(requireActivity()).get(WordViewMudel.class);
        recyclerView = requireActivity().findViewById(R.id.recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        FloatingActionButton floatingActionButton = requireActivity().findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController controller= Navigation.findNavController(v);
                controller.navigate(R.id.action_wordsFragment_to_addFragment);
            }
        });
        adapter1 = new MyAdapter(false,viewMudel);
        adapter2 = new MyAdapter(true,viewMudel);
        dividerItemDecoration=new DividerItemDecoration(requireActivity(),DividerItemDecoration.VERTICAL);
        recyclerView.setItemAnimator(new DefaultItemAnimator(){
            @Override
            public void onAnimationFinished(@NonNull RecyclerView.ViewHolder viewHolder) {
                super.onAnimationFinished(viewHolder);
                LinearLayoutManager linearLayoutManager= (LinearLayoutManager) recyclerView.getLayoutManager();
                if (linearLayoutManager!=null){
                    int firstPosition =linearLayoutManager.findFirstVisibleItemPosition();
                    int lastPosition = linearLayoutManager.findLastVisibleItemPosition();
                    for (int i=firstPosition;i<=lastPosition;i++){
                        MyAdapter.MyViewHolder holder= (MyAdapter.MyViewHolder) recyclerView.findViewHolderForAdapterPosition(i);
                        if (holder!=null){
                            holder.textViewNumber.setText(String.valueOf(i+1));
                        }
                    }
                }
            }
        });
        SharedPreferences   preferences =requireActivity().getSharedPreferences(VIEW_TYPE_SHP,Context.MODE_PRIVATE);
        boolean viewType = preferences.getBoolean(IS_USING_CARDVIEW,false);
       if (viewType){
           recyclerView.setAdapter(adapter2);
           recyclerView.removeItemDecoration(dividerItemDecoration);
       }else {
           recyclerView.setAdapter(adapter1);
           recyclerView.addItemDecoration(dividerItemDecoration);
       }


        filteredWords = viewMudel.getAllWordsLive();
        filteredWords.observe(requireActivity(), new Observer<List<Word>>() {
            @Override
            public void onChanged(List<Word> words) {
                int temp=adapter1.getItemCount();
                allWords=words;
                if (temp!=words.size()){
                    if (temp<words.size()&&!undo){
                        recyclerView.smoothScrollBy(0,-100);
                    }
                    undo=false;
                    adapter1.submitList(words);
                    adapter2.submitList(words);
                }
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main_menu,menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
        searchView.setMaxWidth(1000);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d("myLog","onQueryTextChange"+newText);
                String pattern =newText.trim();
                Log.d("myLog","pattern"+pattern);
                filteredWords.removeObservers(requireActivity());
                filteredWords =viewMudel.findWordsWithPattern(pattern);
                filteredWords.observe(getViewLifecycleOwner(), new Observer<List<Word>>() {
                    @Override
                    public void onChanged(List<Word> words) {
                        int temp=adapter1.getItemCount();
                        allWords=words;
                        if (temp!=words.size()){
                            adapter1.submitList(words);
                            adapter2.submitList(words);
                        }
                    }
                });
                return true;
            }
        });

        //item滑动效果
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                ItemTouchHelper.START|ItemTouchHelper.END) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                final Word wordToDelete=allWords.get(viewHolder.getAdapterPosition());
                viewMudel.deleteWords(wordToDelete);
                Snackbar.make(requireActivity().findViewById(R.id.wordsFragmentView),"删除了一个词汇",Snackbar.LENGTH_SHORT)
                        .setAction("撤销", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                viewMudel.insetWords(wordToDelete);
                                undo=true;
                            }
                        }).show();
            }
            Drawable icon= ContextCompat.getDrawable(requireActivity(),R.drawable.ic_delete_forever_black_24dp);
            Drawable background = new ColorDrawable(Color.LTGRAY);

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                View itemView= viewHolder.itemView;
                int iconMargin=(itemView.getHeight()-icon.getIntrinsicHeight())/2;

                int iconLeft, iconRight, iconTop, iconBottom;
                int backTop, backBottom, backLeft, backRight;
                backTop=itemView.getTop();
                backBottom=itemView.getBottom();
                iconTop=itemView.getTop()+(itemView.getHeight()-icon.getIntrinsicHeight())/2;
                iconBottom=iconTop +icon.getIntrinsicHeight();
                if (dX>0){
                    backLeft=itemView.getLeft();
                    backRight=itemView.getLeft()+(int)dX;
                    background.setBounds(backLeft, backTop, backRight, backBottom);
                    iconLeft=itemView.getLeft()+iconMargin;
                    iconRight=iconLeft+icon.getIntrinsicWidth();
                    icon. setBounds(iconLeft, iconTop, iconRight, iconBottom);
                }else if (dX<0){
                    backRight=itemView.getRight();
                    backLeft=itemView.getRight()+(int)dX;
                    background.setBounds(backLeft, backTop, backRight, backBottom);
                    iconRight=itemView.getRight()-iconMargin;
                    iconLeft=iconRight-icon.getIntrinsicWidth();
                    icon. setBounds(iconLeft, iconTop, iconRight, iconBottom);
                }else {
                    background.setBounds(0,0,0,0);
                    icon.setBounds(0,0,0,0);

                }
                background.draw(c);
                icon.draw(c);
            }
        }).attachToRecyclerView(recyclerView);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.clearData:
                AlertDialog.Builder  builder=new AlertDialog.Builder(requireActivity());
                builder.setTitle("清空数据");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        viewMudel.deleteAllWords();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.create();
                builder.show();
                break;
            case R.id.switchViewType:
                SharedPreferences   preferences =requireActivity().getSharedPreferences(VIEW_TYPE_SHP,Context.MODE_PRIVATE);
                boolean viewType = preferences.getBoolean(IS_USING_CARDVIEW,false);
                SharedPreferences.Editor editor=preferences.edit();
                if (viewType){
                    recyclerView.setAdapter(adapter1);
                    recyclerView.addItemDecoration(dividerItemDecoration);
                    editor.putBoolean(IS_USING_CARDVIEW,false);

                }else {
                    recyclerView.setAdapter(adapter2);
                    recyclerView.removeItemDecoration(dividerItemDecoration);
                    editor.putBoolean(IS_USING_CARDVIEW,true);
                }
                editor.apply();
                break;

            case R.id.inserData:
                String[] english = {
                        "Hello",
                        "World",
                        "Android",
                        "Google",
                        "Studio",

                        "Project",
                        "Database",
                        "Recycler",
                        "View",
                        "String",

                        "Value",
                        "Integer"
                };
                String[] chinese = {
                        "你好",
                        "世界",
                        "安卓系统",
                        "谷歌公司",
                        "工作室",

                        "项目",
                        "数据库",
                        "回收站",
                        "字符串",
                        "视图",
                        "价值",

                        "整数类型"
                };
                for (int i = 0; i < english.length; i++) {
                    viewMudel.insetWords(new Word(english[i], chinese[i], 0));
                }
                break;
        }


        return super.onOptionsItemSelected(item);
    }



}
