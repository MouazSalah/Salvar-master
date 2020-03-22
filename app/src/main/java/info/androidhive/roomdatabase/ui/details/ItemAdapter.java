package info.androidhive.roomdatabase.ui.details;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.recyclerview.extensions.AsyncDifferConfig;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import info.androidhive.roomdatabase.R;
import info.androidhive.roomdatabase.db.entity.ItemEntity;

public class ItemAdapter extends ListAdapter<ItemEntity, ItemAdapter.MyViewHolder>
{
    private static final String TAG = ItemAdapter.class.getSimpleName();

    private Context context;
    private ItemAdapter.ItemsAdapterListener listener;


    public ItemAdapter(@NonNull AsyncDifferConfig<ItemEntity> config) {
        super(config);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        @BindView(R.id.tv_item_date)
        TextView itemDate;

        @BindView(R.id.tv_item_time)
        TextView itemTime;


        @BindView(R.id.old_value_text)
        TextView itemOldValueText;

        @BindView(R.id.tv_item_old_value)
        TextView itemOldValue;

        @BindView(R.id.new_value_text)
        TextView itemNewValueText;

        @BindView(R.id.tv_item_new_value)
        TextView itemNewValue;


        @BindView(R.id.tv_item_difference)
        TextView itemDifference;

        @BindView(R.id.difference_text)
        TextView itemDifferenceText;


        @BindView(R.id.tv_item_cost)
        TextView itemCost;

        @BindView(R.id.cost_text)
        TextView itemCostText;



        public MyViewHolder(View view)
        {
            super(view);
            ButterKnife.bind(this, view);

            view.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    listener.onClick((int) getItem(getLayoutPosition()).getId(), getLayoutPosition());
                }
            });

            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view)
                {
                    listener.onLongClick((int) getItem(getLayoutPosition()).getId(), getLayoutPosition());
                    return true;
                }
            });
        }
    }




     public ItemAdapter(Context context, ItemAdapter.ItemsAdapterListener listener)
    {
        super(DIFF_CALLBACK);
        this.context = context;
        this.listener = listener;
    }

    @Override
    public ItemAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_row, parent, false);

        return new ItemAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ItemAdapter.MyViewHolder holder, int position)
    {
        ItemEntity itemEntity = getItem(position);
        if (itemEntity != null)
        {
            holder.itemDate.setText(itemEntity.getItemDate());
            holder.itemTime.setText(itemEntity.getItemTime());

            holder.itemOldValueText.setText("القديم");
            holder.itemOldValue.setText("" + itemEntity.getItemOldValue());

            holder.itemNewValueText.setText("الجديد");
            holder.itemNewValue.setText("" + itemEntity.getItemNewValue());

            holder.itemDifferenceText.setText("الفرق");
            holder.itemDifference.setText("" + itemEntity.getItemDifferenceValue());

            holder.itemCostText.setText("السعر ");
            holder.itemCost.setText("" + itemEntity.getItemCost());
        }
    }

    private static final DiffUtil.ItemCallback<ItemEntity> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<ItemEntity>()
            {
                @Override
                public boolean areItemsTheSame(@NonNull ItemEntity oldItem, @NonNull ItemEntity newItem)
                {
                    return oldItem.getId() == newItem.getId();
                }

                @Override
                public boolean areContentsTheSame(@NonNull ItemEntity oldItem, @NonNull ItemEntity newItem)
                {
                    return oldItem.getId() == newItem.getId() && oldItem.getItemNewValue() == newItem.getItemNewValue()
                            && oldItem.getItemTime().equals(newItem.getItemNewValue()) && oldItem.getItemDate().equals(newItem.getItemDate());
                }
            };



    public interface ItemsAdapterListener
    {
        void onClick(int ItemId, int position);

        void onLongClick(int ItemId, int position);
    }
}

