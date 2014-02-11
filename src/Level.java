import org.lwjgl.opengl.GL11;


public class Level {
	public Doge doge;
	private float gravity = 1f;
	private Entity[][] entities = new Entity[Config.levelMaxX][Config.levelMaxY];
	
	public float scroll = 0f;
	
	public void setGravity (float _gravity) {
		this.gravity = _gravity;
	}
	
	public float getGravity () {
		return this.gravity;
	}
	
	public void put (Entity box) {
		this.entities[Math.round(box.getPosition().x)][Math.round(box.getPosition().y)] = box;
	}
	
	public Entity get (Vec2f v) {
		if (v.x > -1 && v.x < Config.levelMaxX && v.y > -1 && v.y < Config.levelMaxY)
			return this.entities[(int)(v.x)][(int)(v.y)];
		else
			return new EntityStatic(v.clone());
	}
	
	public void remove (Vec2f v) {
		if (v.x > -1 && v.x < Config.levelMaxX && v.y > -1 && v.y < Config.levelMaxY)
			this.entities[Math.round(v.x)][Math.round(v.y)] = null;
	}
	
	public void remove (Entity box) {
		this.remove(box.getPosition());
	}
	
	public void tick (int delta) {
		//this.scroll += 0.0002 * delta;
		
		for (Entity[] bv : this.entities) {
			for (Entity bi : bv) {
				if (bi != null)
					bi.tick(delta);
			}
		}
		this.doge.tick(delta);
	}
	
	public void render (int delta) {
		GL11.glPushMatrix();
			GL11.glTranslatef(0f, -this.scroll, 0f);
			
			int y0 = (int) this.scroll;
			int y1 = y0 + Config.boxesY + 1;
			
			for (int x = 0; x < Config.boxesX; x++) {
				for (int y = y0; y < y1; y++) {
					if (this.entities[x][y] != null)
						this.entities[x][y].render(delta);
				}
			}
			
			this.doge.render(delta);
		GL11.glPopMatrix();
	}
}
