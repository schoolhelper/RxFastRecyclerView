package androidx.recyclerview.widget;


import android.view.View;
import android.view.ViewGroup;

public class RecyclerView {

    public abstract static class ViewHolder {

        public ViewHolder(View itemView) {
            throw new RuntimeException("Stub!");
        }

        public final int getAdapterPosition() {
            throw new RuntimeException("Stub!");
        }

    }

    public abstract static class Adapter<VH extends ViewHolder> {
        public abstract void onBindViewHolder(VH holder, int position);

        public abstract VH onCreateViewHolder(ViewGroup p0, int p1);

        public abstract int getItemCount();

        public void onViewRecycled(VH viewHolder) {
            throw new RuntimeException("Stub!");
        }

        public final void notifyDataSetChanged() {
            throw new RuntimeException("Stub!");
        }

        public final void notifyItemInserted(int position) {
            throw new RuntimeException("Stub!");
        }

        public final void notifyItemRemoved(int position) {
            throw new RuntimeException("Stub!");
        }

        public final void notifyItemMoved(int fromPosition, int toPosition) {
            throw new RuntimeException("Stub!");
        }
    }

}
