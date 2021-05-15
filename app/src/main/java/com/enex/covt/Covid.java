package com.enex.covt;

public class Covid {
    long confirmed,active,death,recovered;

    public Covid(long confirmed, long active, long death, long recovered) {
        this.confirmed = confirmed;
        this.active = active;
        this.death = death;
        this.recovered = recovered;
    }

    public long getConfirmed() {
        return confirmed;
    }

    public long getActive() {
        return active;
    }

    public long getDeath() {
        return death;
    }

    public long getRecovered() {
        return recovered;
    }
}
