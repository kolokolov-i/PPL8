package superbro.ppl8;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Message {
    NetCom com;
    User fromUser;
    User toUser;
    State state;
    String info;

    public Message(NetCom com, User fromUser, User toUser, State state, String info) {
        this.com = com;
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.info = info;
        this.state = state;
    }

    public Message(NetCom com, User fromUser, State state, String info) {
        this.com = com;
        this.fromUser = fromUser;
        this.toUser = User.None;
        this.state = state;
        this.info = info;
    }

    public Message(NetCom com, User fromUser) {
        this.com = com;
        this.fromUser = fromUser;
        this.toUser = User.None;
        this.state = State.Accept;
        this.info = "";
    }

    public Message(NetCom com, User fromUser, State state) {
        this.com = com;
        this.fromUser = fromUser;
        this.toUser = User.None;
        this.state = state;
        this.info = "";
    }

    public Message(NetCom com, User fromUser, User toUser, State state) {
        this.com = com;
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.info = "";
        this.state = state;
    }

    public Message(DataInputStream inp) throws IOException {
        com = NetCom.values()[inp.readInt()];
        fromUser = User.values()[inp.readInt()];
        toUser = User.values()[inp.readInt()];
        state = State.values()[inp.readInt()];
        info = inp.readUTF();
    }

    public static void connect(User user, DataOutputStream out) throws IOException {
        new Message(NetCom.Connect, user).send(out);
    }

    public static Message take(User user, DataInputStream inp, DataOutputStream out) throws IOException {
        new Message(NetCom.Get, user, User.None, State.None, "").send(out);
        return new Message(inp);
    }

    public static void send(User from, User to, State state, String info, DataOutputStream out) throws IOException {
        new Message(NetCom.Put, from, to, state, info).send(out);
    }

    public static void send(User from, User to, State state, DataOutputStream out) throws IOException {
        new Message(NetCom.Put, from, to, state, "").send(out);
    }

    public void send(DataOutputStream out) throws IOException {
        out.writeInt(com.ordinal());
        out.writeInt(fromUser.ordinal());
        out.writeInt(toUser.ordinal());
        out.writeInt(state.ordinal());
        out.writeUTF(info);
        out.flush();
    }
}
