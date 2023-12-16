package com.example.reminder;
/*reference to set on click listener on delete button: - youtube channel name = coding in flow,recycler view series video 4 and 5
 * */
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdapterReminders extends RecyclerView.Adapter<AdapterReminders.MyViewHolder>{

    private List<Reminders> allReminders;
    private TextView message,time;
    public ImageView deleteReminder;
    private  onItemClickListener mListener;

    public AdapterReminders(List<Reminders> allReminders) {
        this.allReminders = allReminders;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.reminder_item,viewGroup,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        Reminders reminders = allReminders.get(i);
        if(!reminders.getMessage().equals(""))
            message.setText(reminders.getMessage());
        else
            message.setHint("No Message");
        time.setText(reminders.getRemindDate().toString());

    }

    @Override
    public int getItemCount() {
        return allReminders.size();
    }


    public interface onItemClickListener{
        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(onItemClickListener object){
        mListener = object;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.textView1);
            time = itemView.findViewById(R.id.textView21);
            deleteReminder = itemView.findViewById(R.id.ReminderDelete);

            deleteReminder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mListener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            mListener.onDeleteClick(position);
                        }
                    }
                }
            });
        }
    }

}