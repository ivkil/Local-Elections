package org.oporaua.localelections.blanks;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.oporaua.localelections.R;
import org.oporaua.localelections.WebViewActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class BlankAdapter extends RecyclerView.Adapter<BlankAdapter.BlankViewHolder> {

    private final Context mContext;
    private List<Blank> mBlanks;

    public BlankAdapter(Context context) {
        mContext = context;
        mBlanks = getBlanks();
    }

    @Override
    public BlankAdapter.BlankViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.blank_list_item, parent, false);
        return new BlankViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BlankAdapter.BlankViewHolder holder, final int position) {
        final Blank blank = mBlanks.get(position);
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(WebViewActivity.getCallingIntent(mContext, blank.getUri(), true));
            }
        });
        holder.title.setText(blank.getTitle());

    }

    @Override
    public int getItemCount() {
        return mBlanks.size();
    }


    class BlankViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.tv_title)
        TextView title;

        @Bind(R.id.root)
        View root;

        public BlankViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private List<Blank> getBlanks() {
        String[] titles = mContext.getResources().getStringArray(R.array.blank_titles);
        String[] paths = mContext.getResources().getStringArray(R.array.blank_paths);
        List<Blank> blanks = new ArrayList<>();
        for (int i = 0; i < titles.length; i++) {
            blanks.add(new Blank(titles[i], paths[i]));
        }
        return blanks;
    }

}
