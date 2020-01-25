package it.uniba.di.sms1920.madminds.balanceout.ui.detailGroup;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.uniba.di.sms1920.madminds.balanceout.R;
import it.uniba.di.sms1920.madminds.balanceout.model.User;


public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.ViewHolder>{

    List<User> memberList;
    Context context;
    Activity activity;
    String idAdministrator;

    public MemberAdapter(List<User> memberList, Activity activity, String idAdministrator)
    {
        this.memberList = memberList;
        this.activity = activity;
        this.idAdministrator = idAdministrator;
    }

    @Override
    public MemberAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_member,parent,false);
        MemberAdapter.ViewHolder viewHolder = new MemberAdapter.ViewHolder(view);
        context = parent.getContext();
        return viewHolder;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(final MemberAdapter.ViewHolder holder, final int position) {
        final User member = memberList.get(position);

        if(member.getImgUser()!=null) {
            //TODO Insert uri image
        }

        holder.nameMemberTextView.setText(member.getName()+" "+member.getSurname());
        holder.emailMemberTextView.setText(member.getEmail());

        if(!idAdministrator.equals(member.getId())) {
            holder.administratorMemberTextView.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return memberList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        ImageView imgMemberImageView;
        TextView nameMemberTextView;
        TextView emailMemberTextView;
        TextView administratorMemberTextView;
        ConstraintLayout cardMember;

        public ViewHolder(View itemView)
        {
            super(itemView);

            imgMemberImageView = itemView.findViewById(R.id.imgMemberImageView);
            nameMemberTextView = itemView.findViewById(R.id.nameMemberTextView);
            emailMemberTextView = itemView.findViewById(R.id.emailMemberTextView);
            administratorMemberTextView = itemView.findViewById(R.id.administratorMemberTextView);
            cardMember = itemView.findViewById(R.id.memberConstraintLayout);
        }

    }
}