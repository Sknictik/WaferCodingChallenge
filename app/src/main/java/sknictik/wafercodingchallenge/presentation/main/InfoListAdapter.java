package sknictik.wafercodingchallenge.presentation.main;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import sknictik.wafercodingchallenge.R;
import sknictik.wafercodingchallenge.domain.model.Info;

public class InfoListAdapter extends RecyclerView.Adapter<InfoListAdapter.InfoViewHolder> {

    private List<Info> infoList;

    public void setItems(List<Info> infoList) {
        this.infoList = new ArrayList<>(infoList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public InfoListAdapter.InfoViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        return new InfoViewHolder(inflater.inflate(R.layout.item_info, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull InfoViewHolder infoViewHolder, int position) {
        infoViewHolder.bind(infoList.get(position));
    }

    @Override
    public int getItemCount() {
        return infoList != null ? infoList.size() : 0;
    }

    class InfoViewHolder extends RecyclerView.ViewHolder {
        private TextView country;
        private TextView currency;
        private TextView language;

        InfoViewHolder(@NonNull View itemView) {
            super(itemView);
            country = itemView.findViewById(R.id.country_name);
            currency = itemView.findViewById(R.id.currency_name);
            language = itemView.findViewById(R.id.language_name);
        }

        void bind(Info info) {
            country.setText(country.getContext().getString(R.string.country_label, info.getCountryName()));
            currency.setText(currency.getContext().getString(R.string.currency_label, info.getCurrencyName()));
            language.setText(language.getContext().getString(R.string.language_label, info.getLanguageName()));
        }

    }

}
