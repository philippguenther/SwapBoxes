package graphics;
import org.lwjgl.opengl.GL11;

import util.Color4f;
import util.Vec2f;


public class GraphicLineLoop implements Graphic {
	private Vec2f[] vertices;
	private Color4f color;
	private Vec2f offset = new Vec2f(0f, 0f);

	public GraphicLineLoop(Vec2f[] _vertices, Color4f _color) {
		this.vertices = _vertices;
		this.color = _color;
	}

	public GraphicLineLoop(Vec2f[] _vertices, Color4f _color, Vec2f _offset) {
		this.vertices = _vertices;
		this.offset = _offset;
		this.color = _color;
	}
	
	@Override
	public void flipX() {
		//TODO implement
	}
	
	@Override
	public void unflipX() {
		//TODO implement
	}
	
	@Override
	public void reset() {
		// nothing to reset here
	}
	
	@Override
	public boolean disposable() {
		return true;
	}
	
	@Override
	public void tick(int delta) {
		// nothing to do here
	}

	@Override
	public void render() {
		GL11.glPushMatrix();
			GL11.glColor4f(this.color.r, this.color.g, this.color.b, this.color.a);
			GL11.glTranslatef(this.offset.x, this.offset.y, 0f);
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
			GL11.glBegin(GL11.GL_LINE_LOOP);
				for (Vec2f v : this.vertices) {
					GL11.glVertex2f(v.x + this.offset.x, v.y + this.offset.y);
				}
			GL11.glEnd();
		GL11.glPopMatrix();
	}
	
	@Override
	public Graphic clone() {
		return new GraphicLineLoop(this.vertices.clone(), this.color.clone(), this.offset.clone());
	}

	@Override
	public void destroy() {
		this.color = null;
		this.offset = null;
		this.vertices = null;
	}
	
}