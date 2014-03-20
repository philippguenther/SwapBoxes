import util.Vec2f;


public interface Mover {
	public Vec2f getVecDelta(int delta);
	public boolean disposable();
}
