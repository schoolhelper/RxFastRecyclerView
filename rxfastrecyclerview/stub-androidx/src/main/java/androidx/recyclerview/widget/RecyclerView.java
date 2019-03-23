package androidx.recyclerview.widget;

import androidx.annotation.NonNull;

public class RecyclerView {

    public abstract static class ViewHolder {

    }

    public abstract static class Adapter<VH extends ViewHolder> {
        public abstract void onBindViewHolder(@NonNull VH holder, int position);
    }

}
