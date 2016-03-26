package pl.nkg.biblospk.events;

import java.util.ArrayList;
import java.util.List;

public class RenewedEvent {
    final private List<Integer> mRenewsList;
    final private List<Integer> mRenewedList;

    public RenewedEvent(List<Integer> renewsList, List<Integer> renewedList) {
        mRenewsList = renewsList;
        mRenewedList = renewedList;
    }

    public List<Integer> getRenewsList() {
        return mRenewsList;
    }

    public List<Integer> getRenewedList() {
        return mRenewedList;
    }

    public boolean isAllRenewed() {
        return mRenewedList.size() == mRenewsList.size();
    }

    public boolean isNoneRenewed() {
        return mRenewedList.size() == 0;
    }

    public List<Integer> getRenewed() {
        List<Integer> ret = new ArrayList<>();
        for (int a : mRenewsList) {
            if (mRenewedList.contains(a)) {
                ret.add(a);
            }
        }
        return ret;
    }

    public List<Integer> getNoRenewed() {
        List<Integer> ret = new ArrayList<>(mRenewsList);
        ret.removeAll(mRenewedList);
        return ret;
    }
}
