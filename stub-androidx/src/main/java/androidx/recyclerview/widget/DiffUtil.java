package androidx.recyclerview.widget;

public class DiffUtil {

    public static DiffResult calculateDiff(Callback cb, boolean detectMoves) {
        throw new RuntimeException("Stub!");
    }

    public static class DiffResult {
        public void dispatchUpdatesTo(ListUpdateCallback updateCallback) {
            throw new RuntimeException("Stub!");
        }
    }

    public abstract static class Callback {
        public abstract int getOldListSize();

        public abstract int getNewListSize();

        public abstract boolean areItemsTheSame(int oldItemPosition, int newItemPosition);

        public abstract boolean areContentsTheSame(int oldItemPosition, int newItemPosition);

        public Object getChangePayload(int oldItemPosition, int newItemPosition) {
            throw new RuntimeException("Stub!");
        }
    }
}
