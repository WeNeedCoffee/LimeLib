package mrriegel.limelib.tile;

public interface IOwneable {

	public String getOwner();

	public boolean canAccess(String name);

}
