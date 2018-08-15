package sknictik.wafercodingchallenge.presentation.main;

import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import sknictik.wafercodingchallenge.R;
import sknictik.wafercodingchallenge.domain.model.Info;

public class InfoListAdapter extends RecyclerView.Adapter<InfoListAdapter.InfoViewHolder> {

    private List<Info> infoList;

    private OnDeleteButtonClickedListener onDeleteButtonClickedListener;

    InfoListAdapter(final OnDeleteButtonClickedListener onDeleteButtonClickedListener) {
        setHasStableIds(true);
        this.onDeleteButtonClickedListener = onDeleteButtonClickedListener;
    }

    public void setItems(final List<Info> infoList) {
        this.infoList = new ArrayList<>(infoList);
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(final int position) {
        //As far as I understand country name should be unique - best bet for making a unique
        //hashcode but far from ideal of course...
        return infoList.get(position).getCountryName().hashCode();
    }

    @NonNull
    @Override
    public InfoListAdapter.InfoViewHolder onCreateViewHolder(@NonNull final ViewGroup viewGroup, final int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        return new InfoViewHolder(inflater.inflate(R.layout.item_info_closed, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final InfoViewHolder infoViewHolder, final int position) {
        infoViewHolder.bind(infoList.get(position), position, onDeleteButtonClickedListener);
    }

    @Override
    public int getItemCount() {
        return infoList != null ? infoList.size() : 0;
    }

    class InfoViewHolder extends RecyclerView.ViewHolder {
        final ImageView deleteBtn;
        final LinearLayout foreground;
        final TextView country;
        final TextView currency;
        final TextView language;
        final SwipeRevealLayout root;

        InfoViewHolder(@NonNull final View itemView) {
            super(itemView);
            country = itemView.findViewById(R.id.country_name);
            currency = itemView.findViewById(R.id.currency_name);
            language = itemView.findViewById(R.id.language_name);
            foreground = itemView.findViewById(R.id.foreground);
            deleteBtn = itemView.findViewById(R.id.delete_btn);
            root = itemView.findViewById(R.id.root);
        }

        void bind(final Info info, final int position, final OnDeleteButtonClickedListener onDeleteButtonClickedListener) {
            root.close(true);
            country.setText(country.getContext().getString(R.string.country_label, info.getCountryName()));
            currency.setText(currency.getContext().getString(R.string.currency_label, info.getCurrencyName()));
            language.setText(language.getContext().getString(R.string.language_label, info.getLanguageName()));
            foreground.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    //On click closes all opened items
                    //TODO not working when swiping. Try to add notify to swap listener?
                    notifyDataSetChanged();
                }
            });
            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    if (onDeleteButtonClickedListener != null) {
                        onDeleteButtonClickedListener.onDeleteButtonClick(position);
                    }
                }
            });
        }
    }

    public interface OnDeleteButtonClickedListener {
        void onDeleteButtonClick(int position);
    }

}
