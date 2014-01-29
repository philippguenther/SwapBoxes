import java.util.ArrayList;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Color3f;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.contacts.Contact;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;


public class EntityDoge implements Entity {
	private Body body;
	private ArrayList<Graphic> graphics = new ArrayList<Graphic>();
	
	private ArrayList<Entity> contactsFoot = new ArrayList<Entity>();
	private Entity contactL = null;
	private Entity contactR = null;
	
	private int jumpTimerL = 0;
	private int jumpTimerR = 0;
	
	public EntityDoge(Vec2 _pos) {
		BodyDef bdef = new BodyDef();
		bdef.type = BodyType.DYNAMIC;
		bdef.position = _pos;
		bdef.fixedRotation = true;
		bdef.bullet = true;
		this.body = DogeDriller.getGame().getLevel().getWorld().createBody(bdef);
		
		// main fixture
		PolygonShape mainshape = new PolygonShape();
		mainshape.setAsBox(0.35f, 0.4f);
		FixtureDef mainfdef = new FixtureDef();
		mainfdef.shape = mainshape;
		mainfdef.density = 5f;
		mainfdef.friction = 0f;
		mainfdef.userData = this;
		this.body.createFixture(mainfdef);
		this.graphics.add(new GraphicQuad(0.7f, 0.8f, new Color3f(0f, 1f, 0f)));
		
		// sensor fdef
		FixtureDef sensfdef = new FixtureDef();
		sensfdef.density = 0f;
		sensfdef.friction = 0f;
		sensfdef.isSensor = true;
		
		// foot fixture
		PolygonShape footshape = new PolygonShape();
		footshape.setAsBox(0.29f, 0.05f, new Vec2(0f, 0.4f), 0);
		sensfdef.shape = footshape;
		sensfdef.userData = new SensorIdentity(this, Direction.BOTTOM);
		this.body.createFixture(sensfdef);
		this.graphics.add(new GraphicQuad(0.68f, 0.1f, Config.getSensorColor(), new Vec2(0f, 0.4f)));
		
		// head fixture
		PolygonShape headshape = new PolygonShape();
		headshape.setAsBox(0.3f, 0.05f, new Vec2(0f, -0.4f), 0);
		sensfdef.shape = headshape;
		sensfdef.userData = new SensorIdentity(this, Direction.TOP);
		this.body.createFixture(sensfdef);
		this.graphics.add(new GraphicQuad(0.6f, 0.1f, Config.getSensorColor(), new Vec2(0f, -0.4f)));
		
		// left fixture
		PolygonShape leftshape = new PolygonShape();
		leftshape.setAsBox(0.05f, 0.05f, new Vec2(-0.4f, 0.3f), 0);
		sensfdef.shape = leftshape;
		sensfdef.userData = new SensorIdentity(this, Direction.LEFT);
		this.body.createFixture(sensfdef);
		this.graphics.add(new GraphicQuad(0.1f, 0.1f, Config.getSensorColor(), new Vec2(-0.4f, 0.3f)));
		
		// right fixture
		PolygonShape rightshape = new PolygonShape();
		rightshape.setAsBox(0.05f, 0.05f, new Vec2(0.4f, 0.3f), 0);
		sensfdef.shape = rightshape;
		sensfdef.userData = new SensorIdentity(this, Direction.RIGHT);;
		this.body.createFixture(sensfdef);
		this.graphics.add(new GraphicQuad(0.1f, 0.1f, Config.getSensorColor(), new Vec2(0.4f, 0.3f)));
	}

	public ArrayList<Graphic> getGraphics() {
		return this.graphics;
	}
	
	public Vec2 getPosition() {
		return this.body.getPosition();
	}

	public Body getBody() {
		return this.body;
	}
	
	public boolean isOnGround() {
		for (Entity e : this.contactsFoot) {
			if (!(e instanceof EntityDoge))
				return true;
		}
		return false;
	}
	
	public void tick(int delta) {
		if (this.isOnGround()) {
			//left
			if (Keyboard.isKeyDown(Config.keyLeft)) {
				if (this.contactL == null) {
					this.body.setLinearVelocity(new Vec2(-4f, 0.1f));
					this.jumpTimerL = delta;
				} else {
					this.body.setLinearVelocity(new Vec2(0f, 0f));
					this.jumpTimerL += delta;
					if ((this.jumpTimerL > 300 || delta > 300) && this.contactL instanceof EntityBox) {
						EntityBox b = (EntityBox) this.contactL;
						b.print();
						if (b.top == null) {
							this.body.setTransform(new Vec2(this.body.getPosition().x -0.12f, b.getPosition().y - 0.95f), 0);
							this.jumpTimerL = 0;
						}
					}
				}
			// right
			} else if (Keyboard.isKeyDown(Config.keyRight)) {
				if (this.contactR == null) {
					this.body.setLinearVelocity(new Vec2(4f, 0.1f));
					this.jumpTimerR = delta;
				} else {
					this.body.setLinearVelocity(new Vec2(0f, 0f));
					this.jumpTimerR += delta;
					if ((this.jumpTimerR > 300 || delta > 300) && this.contactR instanceof EntityBox) {
						EntityBox b = (EntityBox) this.contactR;
						b.print();
						if (b.top == null) {
							this.body.setTransform(new Vec2(this.body.getPosition().x +0.12f, b.getPosition().y - 0.95f), 0);
							this.jumpTimerR = 0;
						}
					}
				}
			// stop
			} else {
				this.body.setLinearVelocity(new Vec2(0f, 0f));
			}
		// falling
		} else {
			this.body.setLinearVelocity(new Vec2(0, 10f));
		}
	}

	public void render() {
		GL11.glPushMatrix();
			GL11.glTranslatef(this.body.getPosition().x, this.body.getPosition().y, 0f);
			for (Graphic g : this.graphics) {
				g.render();
			}
		GL11.glPopMatrix();
	}

	@Override
	public void beginContact(Contact arg0) {
		Object a = arg0.getFixtureA().getUserData();
		Object b = arg0.getFixtureB().getUserData();
		
		Entity ent = null;
		SensorIdentity sens = null;
		if (a instanceof Entity && b instanceof SensorIdentity) {
			ent = (Entity) a;
			sens = (SensorIdentity) b;
		} else if (b instanceof Entity && a instanceof SensorIdentity) {
			ent = (Entity) b;
			sens = (SensorIdentity) a;
		} else {
			return;
		}
		if (ent instanceof EntityDoge) 
			return;
		else if (!(sens.getEntity() == this))
			return;
		
		// check head contact
		if (sens.getDirection() == Direction.TOP) {
			System.out.println("HEAD hit");
		
		// add to footContacts
		} else if (sens.getDirection() == Direction.BOTTOM && !this.contactsFoot.contains(ent)) {
			this.contactsFoot.add(ent);
		
		// set leftContact
		} else if (sens.getDirection() == Direction.LEFT) {
			this.contactL = ent;
		
		// set rightContact
		} else if (sens.getDirection() == Direction.RIGHT) {
			this.contactR = ent;
		}
	}

	@Override
	public void endContact(Contact arg0) {
		Object a = arg0.getFixtureA().getUserData();
		Object b = arg0.getFixtureB().getUserData();
		
		Entity ent = null;
		SensorIdentity sens = null;
		if (a instanceof Entity && b instanceof SensorIdentity) {
			ent = (Entity) a;
			sens = (SensorIdentity) b;
		} else if (b instanceof Entity && a instanceof SensorIdentity) {
			ent = (Entity) b;
			sens = (SensorIdentity) a;
		} else {
			return;
		}
		if (ent instanceof EntityDoge) 
			return;
		
		// add to footContacts
		if (sens.getDirection() == Direction.BOTTOM) {
			this.contactsFoot.remove(ent);
		
		// set leftContact
		} else if (sens.getDirection() == Direction.LEFT) {
			this.contactL = null;
		
		// set rightContact
		} else if (sens.getDirection() == Direction.RIGHT) {
			this.contactR = null;
		}
	}
	
}
