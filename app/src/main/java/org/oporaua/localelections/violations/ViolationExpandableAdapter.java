package org.oporaua.localelections.violations;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;

import org.oporaua.localelections.R;
import org.oporaua.localelections.violations.model.ViolationChild;
import org.oporaua.localelections.violations.model.ViolationParent;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ViolationExpandableAdapter extends ExpandableRecyclerAdapter<ViolationExpandableAdapter.ViolationParentViewHolder, ViolationExpandableAdapter.ViolationChildViewHolder> {

    private final Context mContext;

    public ViolationExpandableAdapter(Context context, List<ParentListItem> itemList) {
        super(itemList);
        mContext = context;
    }

    @Override
    public ViolationParentViewHolder onCreateParentViewHolder(ViewGroup viewGroup) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.violation_parent, viewGroup, false);
        return new ViolationParentViewHolder(view);
    }

    @Override
    public ViolationChildViewHolder onCreateChildViewHolder(ViewGroup viewGroup) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.violation_child, viewGroup, false);
        return new ViolationChildViewHolder(view);
    }

    @Override
    public void onBindParentViewHolder(final ViolationParentViewHolder parentViewHolder, final int position, final ParentListItem parentListItem) {
        ViolationParent violationParent = (ViolationParent) parentListItem;
        parentViewHolder.mName.setText(violationParent.getName());
        parentViewHolder.mParentDropDownArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (parentViewHolder.isExpanded()) {
                    collapseParent(parentListItem);
                } else {
                    expandParent(parentListItem);
                }
            }
        });
    }

    @Override
    public void onBindChildViewHolder(ViolationChildViewHolder childViewHolder, int i, Object childListItem) {
        final ViolationChild violationChild = (ViolationChild) childListItem;
        childViewHolder.title.setText(violationChild.getName());
        childViewHolder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, violationChild.getName(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    class ViolationChildViewHolder extends ChildViewHolder {

        @Bind(R.id.tv_title)
        TextView title;

        @Bind(R.id.root)
        View root;

        public ViolationChildViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class ViolationParentViewHolder extends ParentViewHolder {

        private static final float INITIAL_POSITION = 0.0f;
        private static final float ROTATED_POSITION = 180f;
        private final boolean mHoneycombAndAbove = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;

        @Bind(R.id.tv_title)
        TextView mName;

        @Bind(R.id.parent_list_item_expand_arrow)
        ImageButton mParentDropDownArrow;

        public ViolationParentViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @SuppressLint("NewApi")
        @Override
        public void setExpanded(boolean expanded) {
            super.setExpanded(expanded);
            if (!mHoneycombAndAbove) return;
            if (expanded) {
                mParentDropDownArrow.setRotation(ROTATED_POSITION);
            } else {
                mParentDropDownArrow.setRotation(INITIAL_POSITION);
            }
        }

    }

}