package com.uksw.khw.whatsapp;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabsAccessorAdapter extends FragmentPagerAdapter
{

    public TabsAccessorAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i)
        {
            case 0:
                ChatsFragment chatsFragment = new ChatsFragment();
                return chatsFragment;

            case 1:
                RequestsFragment requestsFragment= new RequestsFragment();
                return requestsFragment;

            case 2:
                ContactsFragment contactsFragment= new ContactsFragment();
                return contactsFragment;
            case 3:
                GroupFragment groupFragment = new GroupFragment();
                return groupFragment;

                default:
                    return null;
        }

    }

    @Override
    public int getCount()
    {
        return 4;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position)
        {
            case 0:
                return "Chats";

            case 1:
                return "Requests";

            case 2:
                return "Contacts";

            case 3:
                return "Groups";


            default:
                return null;
        }
    }
}
