package meet.lunch.lunchmeet.ui.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import meet.lunch.lunchmeet.R;
import meet.lunch.lunchmeet.ui.utils.RandomColorPicker;

/**
 * Created by Aaron on 7/1/2015.
 *
 * Adapter to popular recycler view
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.VersionViewHolder> {
    List<String> versionModels;
    Boolean isHomeList = false;
    Context context;

    public RandomColorPicker randomColorPicker = new RandomColorPicker();
    public static List<String> homeActivitiesList = new ArrayList<String>();
    public static List<String> homeActivitiesSubList = new ArrayList<String>();
    OnItemClickListener clickListener;

    public RecyclerAdapter(Context context, List<String> versionModels) {
        isHomeList = false;
        this.context = context;
        this.versionModels = versionModels;
    }

    @Override
    public VersionViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerlist_item, viewGroup, false);
        VersionViewHolder viewHolder = new VersionViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(VersionViewHolder versionViewHolder, int i) {
        int randomMaterialColor = randomColorPicker.generateRandomMaterialColor(context);
        versionViewHolder.dateImage.setBackgroundColor(randomMaterialColor);
        versionViewHolder.title.setText(versionModels.get(i));

    }

    @Override
    public int getItemCount() {
        if (isHomeList)
            return homeActivitiesList == null ? 0 : homeActivitiesList.size();
        else
            return versionModels == null ? 0 : versionModels.size();
    }

    class VersionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardView cardItemLayout;
        ImageView dateImage;
        TextView title;
        TextView subTitle;

        public VersionViewHolder(View itemView) {
            super(itemView);

            cardItemLayout = (CardView) itemView.findViewById(R.id.sign_in_cardlist);
            dateImage = (ImageView) itemView.findViewById(R.id.dates_image);
            title = (TextView) itemView.findViewById(R.id.dates_name);
            subTitle = (TextView) itemView.findViewById(R.id.listitem_subname);
        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(v, getPosition());
        }
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void SetOnItemClickListener(final OnItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

}
