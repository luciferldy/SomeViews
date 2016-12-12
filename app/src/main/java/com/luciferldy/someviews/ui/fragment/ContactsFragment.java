package com.luciferldy.someviews.ui.fragment;

import android.Manifest;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.luciferldy.someviews.R;
import com.luciferldy.someviews.ui.view.LetterIndexView;

import java.util.ArrayList;
import java.util.Arrays;

import static android.support.v4.content.PermissionChecker.PERMISSION_DENIED;
import static android.support.v4.content.PermissionChecker.PERMISSION_GRANTED;

/**
 * Created by Lucifer on 2016/12/7.
 */

public class ContactsFragment extends BaseFragment {

    private static final String LOG_TAG = ContactsFragment.class.getSimpleName();
    public static final String TAG = LetterIndexView.class.getSimpleName();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_contact, container, false);
        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission_group.CONTACTS) == PERMISSION_DENIED ||
                ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.WRITE_CONTACTS) == PERMISSION_DENIED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS}, 0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(LOG_TAG, "permissions = " + Arrays.toString(permissions) + ", results = " + Arrays.toString(grantResults));
        if (grantResults[0] == PERMISSION_GRANTED) {
            readContacts();
        }
    }

    /**
     * 读取联系人
     */
    private void readContacts() {
        ContentResolver resolver = getContext().getContentResolver();
        Cursor cursor = resolver.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        if (cursor == null) {
            Log.d(LOG_TAG, "cursor is null.");
            return;
        }
        if (cursor.getCount() > 0) {
            ArrayList<ContactsBody> contacts = new ArrayList<>();
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                Log.d(LOG_TAG, "id = " + id + ", name = " + name);
                ContactsBody contact = new ContactsBody();
                contact.name = name;
                if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    // Query phone here
                    Cursor pCur = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    if (pCur == null) {
                        Log.d(LOG_TAG, "pCur is null");
                        continue;
                    }
                    while (pCur.moveToNext()) {
                        // Do something with phones
                        contact.number = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        break;
                    }
                    pCur.close();

                }
                contacts.add(contact);
            }

            if (contacts.size() > 0) {
                initRecyclerView(contacts);

            }
        }
        cursor.close();

    }

    private void initViews() {
        if (getView() == null)
            return;
        final TextView tvShow = (TextView) getView().findViewById(R.id.tv_show);
        LetterIndexView liv = (LetterIndexView) getView().findViewById(R.id.liv_contacts);
        liv.setClickArea(new LetterIndexView.ClickArea() {
            @Override
            public void clickPosition(int position, String value) {
                tvShow.setText(value);
            }

            @Override
            public void show() {
                tvShow.setVisibility(View.VISIBLE);
            }

            @Override
            public void hide() {
                tvShow.setVisibility(View.GONE);
            }
        });
    }

    /**
     * 初始化 RecyclerView
     * @param contacts 联系人列表
     */
    private void initRecyclerView(ArrayList<ContactsBody> contacts) {
        if (getView() == null)
            return;
        RecyclerView rv = (RecyclerView) getView().findViewById(R.id.rv_contacts);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        ContactsAdapter adapter = new ContactsAdapter(contacts);
        rv.setAdapter(adapter);
    }

    class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactsViewHolder> {

        private ArrayList<ContactsBody> mContacts = new ArrayList<>();

        public ContactsAdapter(ArrayList<ContactsBody> contacts) {
            super();
            mContacts.addAll(contacts);
            Log.d(LOG_TAG, "ContactAdapter constructor " + contacts.size());
        }

        @Override
        public ContactsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Log.d(LOG_TAG, "onCreateViewHolder");
            View root = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item, parent, false);
            ContactsViewHolder holder = new ContactsViewHolder(root);
            return holder;
        }

        @Override
        public void onBindViewHolder(ContactsViewHolder holder, int position) {
            Log.d(LOG_TAG, "onBindViewHolder");
            holder.tvName.setText(mContacts.get(position).name);
            holder.tvPNumber.setText(mContacts.get(position).number);
        }

        @Override
        public int getItemCount() {
            Log.d(LOG_TAG, "getItemCount " + mContacts.size());
            return mContacts.size();
        }

        class ContactsViewHolder extends RecyclerView.ViewHolder {

            TextView tvName;
            TextView tvPNumber;

            public ContactsViewHolder(View itemView) {
                super(itemView);
                tvName = (TextView) itemView.findViewById(R.id.name);
                tvPNumber = (TextView) itemView.findViewById(R.id.number);
            }
        }
    }

    class ContactsBody {
        public String name;
        public String number;
    }
}
