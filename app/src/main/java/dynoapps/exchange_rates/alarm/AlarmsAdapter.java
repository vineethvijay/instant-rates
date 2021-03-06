package dynoapps.exchange_rates.alarm;

import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import dynoapps.exchange_rates.App;
import dynoapps.exchange_rates.R;
import dynoapps.exchange_rates.SourcesManager;
import dynoapps.exchange_rates.data.CurrencySource;
import dynoapps.exchange_rates.event.AlarmUpdateEvent;
import dynoapps.exchange_rates.ui.widget.recyclerview.UpdatableAdapter;
import dynoapps.exchange_rates.util.CollectionUtils;
import dynoapps.exchange_rates.util.RateUtils;

/**
 * Created by erdemmac on 14/12/2016.
 */

class AlarmsAdapter extends UpdatableAdapter<List<Alarm>, AlarmsActivity.AlarmViewHolder> {
    private List<Alarm> alarms;
    private AlarmsRepository alarmRepository;

    AlarmsAdapter(AlarmsRepository alarmsRepository,ArrayList<Alarm> alarms) {
        this.alarmRepository = alarmsRepository;
        this.alarms = new ArrayList<>();
        this.alarms.addAll(alarms);
    }

    private int getToAddIndex(Alarm alarm) {
        if (CollectionUtils.isNullOrEmpty(alarms)) return 0;
        for (int i = 0; i < alarms.size(); i++) {
            Alarm innerAlarm = alarms.get(i);
            if (Alarm.COMPARATOR.compare(alarm, innerAlarm) < 0) {
                return Math.max(0, i);
            }
        }
        return CollectionUtils.size(alarms);
    }

    public void addData(Alarm alarm) {
        if (this.alarms == null) this.alarms = new ArrayList<>();
        int index = getToAddIndex(alarm);
        this.alarms.add(index, alarm);
        notifyItemInserted(index);
    }

    public void addData(List<Alarm> alarms) {
        if (this.alarms == null) this.alarms = new ArrayList<>();
        final int insertRangeStart = getItemCount();
        this.alarms.addAll(alarms);
        notifyItemRangeInserted(insertRangeStart, getItemCount());
    }

    @Override
    public AlarmsActivity.AlarmViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alarm, parent, false);
        return new AlarmsActivity.AlarmViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final AlarmsActivity.AlarmViewHolder holder, int position) {
        final Alarm alarm = alarms.get(position);
        holder.ivType.setRotation(alarm.is_above ? 90 : 270);
        holder.ivType.setColorFilter(ContextCompat.getColor(App.context(), alarm.is_above ?
                android.R.color.holo_green_light :
                android.R.color.holo_red_light));
        holder.tvTypeHint.setText(alarm.is_above ? R.string.if_above : R.string.if_below);
        holder.tvValue.setText(RateUtils.valueToUI(alarm.val, alarm.rate_type));
        holder.ivRateType.setImageResource(RateUtils.getRateIcon(alarm.rate_type));
        CurrencySource source = SourcesManager.getSource(alarm.source_type);
        if (source != null) {
            holder.tvSource.setText(source.getName());
            holder.tvSource.setTextColor(source.getColor());
        } else {
            holder.tvSource.setText("-");
            holder.tvSource.setTextColor(ContextCompat.getColor(App.context(), R.color.colorPrimary));
        }

        holder.vClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = holder.getAdapterPosition();
                AlarmsAdapter.this.alarms.remove(pos);
                notifyItemRemoved(pos);
                alarmRepository.deleteAlarm(alarm,null);
            }
        });
        holder.swAlarm.setChecked(alarm.is_enabled);
        holder.swAlarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                alarm.is_enabled = b;
                alarmRepository.updateAlarm(alarm,null);
                EventBus.getDefault().post(new AlarmUpdateEvent());
            }
        });
    }

    @Override
    public int getItemCount() {
        return alarms != null ? alarms.size() : 0;
    }

    @Override
    public void update(@NonNull List<Alarm> updatedData) {
        alarms = updatedData;
        notifyDataSetChanged();
    }
}