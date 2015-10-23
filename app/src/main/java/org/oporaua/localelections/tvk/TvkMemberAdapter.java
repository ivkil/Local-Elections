package org.oporaua.localelections.tvk;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.oporaua.localelections.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TvkMemberAdapter extends RecyclerView.Adapter<TvkMemberAdapter.TvkMemberViewHolder> {

    private final Context mContext;
    private List<TvkMember> mTvkMembers;

    public TvkMemberAdapter(Context context) {
        mContext = context;
        mTvkMembers = new ArrayList<>();
    }

    @Override
    public TvkMemberViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.tvk_member_list_item, parent, false);
        return new TvkMemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TvkMemberViewHolder holder, final int position) {
        final TvkMember member = mTvkMembers.get(position);
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mContext.startActivity(WebViewActivity.getCallingIntent(mContext, blank.getUri(), true));
            }
        });
        holder.name.setText(member.getName());

    }

    @Override
    public int getItemCount() {
        return mTvkMembers.size();
    }

    class TvkMemberViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.tv_name)
        TextView name;

        @Bind(R.id.root)
        View root;

        public TvkMemberViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void swapData(List<TvkMember> members) {
        mTvkMembers = members;
        notifyDataSetChanged();
    }

    public void clearAll() {
        mTvkMembers = new ArrayList<>();
        notifyDataSetChanged();
    }
}
