package com.example.mywords;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

public class MyAdapter extends ListAdapter<Word,MyAdapter.MyViewHolder> {

private boolean useCardView;
private WordViewMudel viewMudel;

    MyAdapter(boolean useCardView, WordViewMudel viewMudel) {
        super(new DiffUtil.ItemCallback<Word>() {
            @Override
            public boolean areItemsTheSame(@NonNull Word oldItem, @NonNull Word newItem) {
                return oldItem.getId()==newItem.getId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull Word oldItem, @NonNull Word newItem) {
                return (oldItem.getWord().equals(newItem.getWord())&&oldItem.getChineseInbisible()
                .equals(newItem.getChineseInbisible())
                &&newItem.getMeaning().equals(oldItem.getMeaning()));
            }
        });
        this.useCardView = useCardView;
        this.viewMudel=viewMudel;
    }



    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =LayoutInflater.from(parent.getContext());
        View itemView;
        if (useCardView){
            itemView = layoutInflater.inflate(R.layout.cell_card2,parent,false);
        }else {
            itemView = layoutInflater.inflate(R.layout.cell_normal2,parent,false);
        }
        final MyViewHolder holder =new MyViewHolder(itemView);

//        holder.aSwitchChinesInvisible.setOnCheckedChangeListener(null);
        holder.aSwitchChinesInvisible.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Word word=(Word)holder.itemView.getTag(R.id.word_for_view_hord);
                if (isChecked){
                    holder.textViewChinese.setVisibility(View.GONE);
                    word.setChineseInbisible(1);
                    viewMudel.updataWords(word);
                }else {
                    holder.textViewChinese.setVisibility(View.VISIBLE);
                    word.setChineseInbisible(0);
                    viewMudel.updataWords(word);
                }
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri =Uri.parse("https://fanyi.baidu.com/?fr=websearch_submit&pid=sogou-addr-cc9657884708170e#en/zh/"
                        +holder.textViewEnglish.getText());
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(uri);
                holder.itemView.getContext().startActivity(intent);
            }
        });
        return holder;
        //return null;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

        final Word word = getItem(position);
        holder.itemView.setTag(R.id.word_for_view_hord,word);
        holder.textViewNumber.setText(String.valueOf(position+1));
        holder.textViewEnglish.setText(word.getWord());
        holder.textViewChinese.setText(word.getMeaning());
        if (word.getChineseInbisible()==1){
            holder.textViewChinese.setVisibility(View.GONE);
            holder.aSwitchChinesInvisible.setChecked(true);
        }else {
            holder.textViewChinese.setVisibility(View.VISIBLE);
            holder.aSwitchChinesInvisible.setChecked(false);
        }


    }

    @Override
    public void onViewAttachedToWindow(@NonNull MyViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        holder.textViewNumber.setText(String.valueOf(holder.getAdapterPosition()+1));
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
    TextView textViewNumber;
        TextView textViewEnglish;
        TextView textViewChinese;
    private Switch aSwitchChinesInvisible;
        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNumber =itemView.findViewById(R.id.textViewnumber);
            textViewEnglish = itemView.findViewById(R.id.textViewenglish);
            textViewChinese =itemView.findViewById(R.id.textViewchinese);
            aSwitchChinesInvisible=itemView.findViewById(R.id.switch2);
        }
    }
}
