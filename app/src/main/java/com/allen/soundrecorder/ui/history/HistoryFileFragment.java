package com.allen.soundrecorder.ui.history;

import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.allen.soundrecorder.R;
import com.allen.soundrecorder.db.Recording;
import com.allen.soundrecorder.db.RecordingViewModel;
import com.allen.soundrecorder.ui.adapter.HistoryFileAdapter;
import com.allen.soundrecorder.ui.playback.PlaybackFragment;
import com.allen.soundrecorder.ui.playback.PlaybackPresenter;

import java.util.List;

import static com.allen.soundrecorder.utils.Preconditions.checkNotNull;

/**
 * author:  Allen <br>
 * date:  2018/11/21 18:15<br>
 * description:Management history file
 */
public class HistoryFileFragment extends Fragment implements HistoryFileContract.View {
    private static final String TAG = HistoryFileFragment.class.getSimpleName();
    private RecordingViewModel mViewModel;
    private CharSequence[] mOptionItems;
    private HistoryFileContract.Presenter mPresenter;

    public static HistoryFileFragment newInstance() {
        return new HistoryFileFragment();
    }

    private static final int DIALOG_FILE_RENAME = 0;
    private static final int DIALOG_FILE_DELETE = 1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mOptionItems = new CharSequence[]{getString(R.string.dialog_file_rename),
                getString(R.string.dialog_file_delete)};
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View historyFileView = inflater.inflate(R.layout.fragment_history_file, container, false);

        RecyclerView recyclerView = historyFileView.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(llm);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        final HistoryFileAdapter adapter = new HistoryFileAdapter(getContext());
        recyclerView.setAdapter(adapter);
        adapter.setCallback(new HistoryFileAdapter.Callback() {
            @Override
            public void itemOnLongClick(Recording item) {
                showOptionsDialog(item);
            }

            @Override
            public void itemOnClick(Recording item) {
                showPlaybackDialog(item);
            }
        });
        // Get a new or existing ViewModel from the ViewModelProvider.
        mViewModel = ViewModelProviders.of(this).get(RecordingViewModel.class);
        mViewModel.getAllRecordings().observe(this, new Observer<List<Recording>>() {
            @Override
            public void onChanged(@Nullable List<Recording> recordings) {
                adapter.setRecordings(recordings);
            }
        });
        return historyFileView;
    }

    @Override
    public void setPresenter(HistoryFileContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    public void addRecording(Recording item) {
        mViewModel.addRecording(item);
    }

    @Override
    public void showOptionsDialog(final Recording item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.dialog_title_options));
        builder.setItems(mOptionItems, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int option) {
                if (option == DIALOG_FILE_RENAME) {
                    showRenameDialog(item);
                } else if (option == DIALOG_FILE_DELETE) {
                    showRemoveDialog(item);
                }
            }
        });
        builder.setCancelable(true);
        builder.setNegativeButton(getString(R.string.dialog_action_cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void showRenameDialog(final Recording item) {
        AlertDialog.Builder renameFileBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.dialog_rename_file, null);
        final EditText input = view.findViewById(R.id.new_name);
        renameFileBuilder.setTitle(getString(R.string.dialog_title_rename));
        renameFileBuilder.setCancelable(true);
        renameFileBuilder.setPositiveButton(getString(R.string.dialog_action_ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            String name = input.getText().toString().trim();
                            if (TextUtils.isEmpty(name)) {
                                showToastTips(getString(R.string.dialog_name_is_empty));
                                return;
                            }
                            item.setName(name + ".mp4");
                            mPresenter.rename(item);
                        } catch (Exception e) {
                            Log.e(TAG, "An exception has occurred in rename func", e);
                        }
                        dialog.cancel();
                    }
                });
        renameFileBuilder.setNegativeButton(getString(R.string.dialog_action_cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        renameFileBuilder.setView(view);
        AlertDialog alert = renameFileBuilder.create();
        alert.show();
    }

    @Override
    public void showRemoveDialog(final Recording item) {
        AlertDialog.Builder confirmDelete = new AlertDialog.Builder(getActivity());
        confirmDelete.setTitle(getString(R.string.dialog_title_delete));
        confirmDelete.setMessage(getString(R.string.dialog_text_delete));
        confirmDelete.setCancelable(true);
        confirmDelete.setPositiveButton(getString(R.string.dialog_action_ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            mPresenter.remove(item);
                        } catch (Exception e) {
                            Log.e(TAG, "An exception has occurred in remove func", e);
                        }
                        dialog.cancel();
                    }
                });
        confirmDelete.setNegativeButton(getString(R.string.dialog_action_cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = confirmDelete.create();
        alert.show();
    }

    @Override
    public void showPlaybackDialog(Recording item) {
        PlaybackFragment playbackFragment =
                new PlaybackFragment().newInstance(item);
        new PlaybackPresenter(playbackFragment, item.getFilePath());
        FragmentTransaction transaction = (getActivity())
                .getSupportFragmentManager()
                .beginTransaction();
        playbackFragment.show(transaction, "dialog_playback");
    }

    @Override
    public void showFileExistTips(String name) {
        showToastTips(String.format(getString(R.string.toast_file_exists), name));
    }

    @Override
    public void showToastTips(String tips) {
        Toast.makeText(getActivity(),tips,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public RecordingViewModel getViewMode() {
        return mViewModel;
    }
}
