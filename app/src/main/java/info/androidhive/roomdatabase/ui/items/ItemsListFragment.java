package info.androidhive.roomdatabase.ui.items;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import info.androidhive.roomdatabase.ui.viewmodel.NotesListViewModel;
import info.androidhive.roomdatabase.R;
import info.androidhive.roomdatabase.db.entity.NoteEntity;
import info.androidhive.roomdatabase.ui.details.ItemsActivity;

public class ItemsListFragment extends Fragment implements ItemsAdapter.NotesAdapterListener
{
    public static final String TAG = ItemsListFragment.class.getSimpleName();
    private NotesListViewModel viewModel;
    private ItemsAdapter mAdapter;
    private Unbinder unbinder;

    double flatCost;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.txt_empty_notes_view)
    TextView noNotesView;

    public ItemsListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_note_list, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        viewModel = ViewModelProviders.of(getActivity()).get(NotesListViewModel.class);

        mAdapter = new ItemsAdapter(getActivity(), this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewModel.getNotes().observe(this, new Observer<List<NoteEntity>>() {
            @Override
            public void onChanged(@Nullable List<NoteEntity> notes) {
                mAdapter.submitList(notes);
                toggleEmptyNotes(notes.size());
            }
        });
    }

    private void toggleEmptyNotes(int size) {
        if (size > 0) {
            noNotesView.setVisibility(View.GONE);
        } else {
            noNotesView.setVisibility(View.VISIBLE);
        }
    }

    public void deleteAllNotes()
    {

        viewModel.deleteAllNotes();
    }

    /**
     * Shows alert dialog with EditText options to enter / edit
     * a note.
     * when shouldUpdate=true, it automatically displays old note and changes the
     * button text to UPDATE
     */
    public void showNoteDialog(final boolean shouldUpdate, final NoteEntity note, final int position)
    {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getContext());
        View view = layoutInflaterAndroid.inflate(R.layout.note_dialog, null);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(getContext());
        alertDialogBuilderUserInput.setView(view);

        final EditText inputNote = view.findViewById(R.id.note);
        final EditText inputCost = view.findViewById(R.id.note_cost);
        TextView dialogTitle = view.findViewById(R.id.dialog_title);
        dialogTitle.setText(!shouldUpdate ? getString(R.string.lbl_new_note_title) : getString(R.string.lbl_edit_note_title));

        if (shouldUpdate && note != null) {
            // append sets text to EditText and places the cursor at the end
            inputNote.append(note.getNote());
            inputCost.append("" + note.getNoteCost());
        }
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton(shouldUpdate ? getString(R.string.update) : getString(R.string.save), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {

                    }
                })
                .setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });

        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show toast message when no text is entered
                if (TextUtils.isEmpty(inputNote.getText().toString()) && TextUtils.isEmpty(inputCost.getText().toString()))
                {
                    Toast.makeText(getActivity(), getString(R.string.dialog_title_enter_note), Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    alertDialog.dismiss();
                }

                // check if user updating note
                if (shouldUpdate && note != null )
                {
                    note.setNote(inputNote.getText().toString());
                    note.setNoteCost(Double.parseDouble(inputCost.getText().toString()));
                    viewModel.updateNote(note);
                }
                else
                 {
                    // create new note
                     if (!inputNote.getText().toString().isEmpty() && !inputCost.getText().toString().isEmpty())
                     {
                         flatCost  = Double.parseDouble(inputCost.getText().toString());
                         NoteEntity note = new NoteEntity(inputNote.getText().toString(), flatCost);
                         viewModel.insertNote(note);
                         Log.d("item cost" , "" + flatCost);
                     }
                     else
                     {
                         Toast.makeText(getActivity(), "برجاء ادخال البيانات", Toast.LENGTH_SHORT).show();
                     }

                }
            }
        });
    }

    /**
     * Opens dialog with Edit - Delete options
     * Edit - 0
     * Delete - 0
     */
    private void showActionsDialog(int noteId, final int position)
    {
        // fetch the note from db
        final NoteEntity note;
        try {
            note = viewModel.getNote(noteId);

            CharSequence colors[] = new CharSequence[]{getString(R.string.edit), getString(R.string.delete)};

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(getString(R.string.dialog_title_choose));
            builder.setItems(colors, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == 0)
                    {
                        showNoteDialog(true, note, position);
                    }
                    else
                    {
                        showAlertDialog(note);
                    }
                }
            });
            builder.show();

        } catch (ExecutionException e) {
            // TODO - handle error
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO - handle error
            e.printStackTrace();
        }
    }

    public void showAlertDialog(final NoteEntity noteEntity)
    {
        new AlertDialog.Builder(getActivity()).setTitle("رسالة تحذير")
                .setMessage("هل تريد حذف ذلك العنصر؟")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        viewModel.deleteNote(noteEntity);
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


    @Override
    public void onClick(int noteId, double noteCost, int position)
    {
        Intent intent = new Intent(getActivity(), ItemsActivity.class);
        intent.putExtra("flat_id", noteId);
        intent.putExtra("item_cost", noteCost);
        startActivity(intent);
        Log.d("cost" , "" + flatCost);
    }

    @Override
    public void onLongClick(int noteId, int position) {
        showActionsDialog(noteId, position);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
