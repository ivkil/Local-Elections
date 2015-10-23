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
        holder.name.setText(member.getName());
        holder.yeah.setText(String.valueOf(member.getYear()));
        holder.election.setText(member.getElections());
        holder.region.setText(member.getRegion());
        holder.commission.setText(member.getCommission());
        holder.position.setText(member.getPosition());
        holder.party.setText(member.getParty());
    }

    @Override
    public int getItemCount() {
        return mTvkMembers.size();
    }

    class TvkMemberViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.tv_name)
        TextView name;

        @Bind(R.id.tv_yeah)
        TextView yeah;

        @Bind(R.id.tv_elections)
        TextView election;

        @Bind(R.id.tv_region)
        TextView region;

        @Bind(R.id.tv_commission)
        TextView commission;

        @Bind(R.id.tv_position)
        TextView position;

        @Bind(R.id.tv_party)
        TextView party;

        public TvkMemberViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void swapData(List<TvkMember> members) {
        if (members != null) {
            mTvkMembers = members;
        } else {
            mTvkMembers = new ArrayList<>();
        }
        notifyDataSetChanged();
    }

    public void clearAll() {
        mTvkMembers = new ArrayList<>();
        notifyDataSetChanged();
    }

    public List<TvkMember> getData() {
        return mTvkMembers;
    }

}
