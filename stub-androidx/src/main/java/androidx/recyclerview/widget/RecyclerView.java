package androidx.recyclerview.widget;


import android.view.View;

public class RecyclerView {

    public abstract static class ViewHolder {

        public ViewHolder(View itemView) {
            throw new IllegalArgumentException("");
        }
    }

    public abstract static class Adapter<VH extends ViewHolder> {
        public abstract void onBindViewHolder(VH holder, int position);
    }

}
