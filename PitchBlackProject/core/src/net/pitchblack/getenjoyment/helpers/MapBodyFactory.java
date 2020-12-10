package net.pitchblack.getenjoyment.helpers;

import java.util.ArrayList;

import com.badlogic.gdx.maps.Map;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.CircleMapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;

import net.pitchblack.getenjoyment.entities.BodyFactory;
import net.pitchblack.getenjoyment.logic.GameWorld;

/*
 * Adapted from: <a href = "https://github.com/dsaltares/sioncore/blob/master/src/com/siondream/core/physics/MapBodyManager.java"> MapBodyManager.java by David Saltares </a>
 */
public class MapBodyFactory {

    private static final float TILE_SIZE = 32f;

	public static ArrayList<Body> getCollisionBodies(Map map, World world) {
		MapObjects objects = map.getLayers().get("collisionObjLayer").getObjects();

		ArrayList<Body> bodies = new ArrayList<Body>();

		for (MapObject object : objects) {

			if (object instanceof TextureMapObject) {
				continue;
			}

			Shape shape;
			Vector2 position;

			if (object instanceof RectangleMapObject) {
				shape = getRectangle((RectangleMapObject) object);
				position = new Vector2(
						((RectangleMapObject) object).getRectangle().getX() / GameWorld.PPM ,
						((RectangleMapObject) object).getRectangle().getY() / GameWorld.PPM
						);
			} else if (object instanceof PolygonMapObject) {
				shape = getPolygon((PolygonMapObject) object);
				position = new Vector2(
						((PolygonMapObject) object).getPolygon().getX(),
						((PolygonMapObject) object).getPolygon().getY()
						);
			} else if (object instanceof PolylineMapObject) {
				shape = getPolyline((PolylineMapObject) object);
				position = new Vector2(
						((PolylineMapObject) object).getPolyline().getX(),
						((PolylineMapObject) object).getPolyline().getY()
						);
			} else if (object instanceof CircleMapObject) {
				shape = getCircle((CircleMapObject) object);
				position = new Vector2(
						((CircleMapObject) object).getCircle().x,
						((CircleMapObject) object).getCircle().y
						);
			} else {
				continue;
			}
			
			
			BodyDef bodyDef = new BodyDef();
			bodyDef.type = BodyType.StaticBody;
			bodyDef.position.set(position);
			Body body = world.createBody(bodyDef);
			
			FixtureDef fixtureDef = new FixtureDef();
			fixtureDef.shape = shape;
			fixtureDef.friction = 0f;
			body.createFixture(fixtureDef);

			body.setUserData(GameWorld.MAP_USER_DATA);
			bodies.add(body);
			
			
			//body.setTransform(position, 0f);

			shape.dispose();
		}
		return bodies;
	}

	private static PolygonShape getRectangle(RectangleMapObject rectangleObject) {
		Rectangle rectangle = rectangleObject.getRectangle();
		PolygonShape polygonShape = new PolygonShape();
		
		Vector2 center = new Vector2(rectangle.width / 2 / GameWorld.PPM,
									 rectangle.height / 2 / GameWorld.PPM 
				);
		
		polygonShape.setAsBox(rectangle.width / 2 / GameWorld.PPM,
							  rectangle.height / 2 / GameWorld.PPM,
							  center,
							  0.0f
				);
		
		return polygonShape;
	}
	
//	private static PolygonShape getRectangle(RectangleMapObject rectangleObject) {
//        Rectangle rectangle = rectangleObject.getRectangle();
//        PolygonShape polygon = new PolygonShape();
//        Vector2 size = new Vector2((rectangle.x * 0.5f) / TILE_SIZE,
//                                   (rectangle.y  * 0.5f ) / TILE_SIZE);
//        polygon.setAsBox(rectangle.width * 0.5f ,
//                         rectangle.height * 0.5f ,
//                         size,
//                         0.0f);
//        return polygon;
//    }

	private static CircleShape getCircle(CircleMapObject circleObject) {
		Circle circle = circleObject.getCircle();
		CircleShape circleShape = new CircleShape();
		circleShape.setRadius(circle.radius / TILE_SIZE);
		circleShape.setPosition(new Vector2(circle.x / TILE_SIZE, circle.y / TILE_SIZE));
		return circleShape;
	}

	private static PolygonShape getPolygon(PolygonMapObject polygonObject) {
		PolygonShape polygon = new PolygonShape();
		float[] vertices = polygonObject.getPolygon().getTransformedVertices();

		float[] worldVertices = new float[vertices.length];

		for (int i = 0; i < vertices.length; ++i) {
			System.out.println(vertices[i]);
			worldVertices[i] = vertices[i] / TILE_SIZE;
		}

		polygon.set(worldVertices);
		return polygon;
	}

	private static ChainShape getPolyline(PolylineMapObject polylineObject) {
		float[] vertices = polylineObject.getPolyline().getTransformedVertices();
		Vector2[] worldVertices = new Vector2[vertices.length / 2];

		for (int i = 0; i < vertices.length / 2; ++i) {
			worldVertices[i] = new Vector2();
			worldVertices[i].x = vertices[i * 2] / TILE_SIZE;
			worldVertices[i].y = vertices[i * 2 + 1] / TILE_SIZE;
		}

		ChainShape chain = new ChainShape();
		chain.createChain(worldVertices);
		return chain;
	}
}
