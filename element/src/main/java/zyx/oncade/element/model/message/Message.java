package zyx.oncade.element.model.message;

public class Message {

    private Integer id;
    private String message;
    private long created;
    private long updated;

    public Integer getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public long getCreated() {
        return created;
    }

    public long getUpdated() {
        return updated;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setMessage(Message message) {
        this.id = message.id;
        this.message = message.message;
        this.created = message.created;
        this.updated = message.updated;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public void setUpdated(long updated) {
        this.updated = updated;
    }

    @Override
    public String toString() {
        return "Message [id=" + id + ", message=" + message + ", created=" + created + ", updated=" + updated + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((message == null) ? 0 : message.hashCode());
        result = prime * result + (int) (created ^ (created >>> 32));
        result = prime * result + (int) (updated ^ (updated >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Message other = (Message) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (message == null) {
            if (other.message != null)
                return false;
        } else if (!message.equals(other.message))
            return false;
        if (created != other.created)
            return false;
        if (updated != other.updated)
            return false;
        return true;
    }

}
