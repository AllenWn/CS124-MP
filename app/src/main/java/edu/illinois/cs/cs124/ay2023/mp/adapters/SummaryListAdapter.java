package edu.illinois.cs.cs124.ay2023.mp.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import edu.illinois.cs.cs124.ay2023.mp.R;
import edu.illinois.cs.cs124.ay2023.mp.models.Summary;
import java.util.List;
import java.util.function.Consumer;

/**
 * Adapter to display a list of summaries using Android's RecyclerView.
 *
 * <p>You should not need to modify this code, although you may want to.
 *
 * @noinspection unused
 */
public class SummaryListAdapter extends RecyclerView.Adapter<SummaryListAdapter.ViewHolder> {

  private List<Summary> summaries;

  private final Consumer<Summary> onClickCallback;

  private final Activity activity;

  public SummaryListAdapter(
      @NonNull List<Summary> setSummaries,
      @NonNull Activity setActivity,
      @Nullable Consumer<Summary> setOnClickCallback) {
    summaries = setSummaries;
    onClickCallback = setOnClickCallback;
    activity = setActivity;
  }

  public SummaryListAdapter(@NonNull List<Summary> setSummaries, @NonNull Activity setActivity) {
    this(setSummaries, setActivity, null);
  }

  @SuppressLint("NotifyDataSetChanged")
  public void setSummaries(@NonNull List<Summary> setSummaries) {
    summaries = setSummaries;
    activity.runOnUiThread(this::notifyDataSetChanged);
  }

  @NonNull
  @Override
  public SummaryListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view =
        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_summary, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull SummaryListAdapter.ViewHolder holder, int position) {
    Summary summary = summaries.get(position);
    // Set the title text as the result of calling toString
    holder.title.setText(summary.toString());
    holder.layout.setOnClickListener(
        view -> {
          if (onClickCallback != null) {
            onClickCallback.accept(summary);
          }
        });
  }

  @Override
  public int getItemCount() {
    return summaries.size();
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {
    private final LinearLayout layout;
    private final TextView title;

    public ViewHolder(@NonNull View itemView) {
      super(itemView);
      layout = itemView.findViewById(R.id.layout);
      title = itemView.findViewById(R.id.title);
    }
  }
}
