package com.example.archaeologyar;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.ar.core.Anchor;
import com.google.ar.core.Frame;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;

import java.io.File;
import java.util.concurrent.CompletableFuture;

public class MainActivity extends AppCompatActivity {

    Node infoCard = new Node();
    private ArFragment arFragment;
    private CompletableFuture<ModelRenderable> objectRenderable;
    private CompletableFuture<ViewRenderable> infocardRendrable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.arFragment);

        if (objectRenderable == null) {
            objectRenderable =
                    ModelRenderable.builder()
                            .setSource(this, Uri.parse("model.sfb"))
                            .build();
            infocardRendrable =
                    ViewRenderable.builder()
                            .setView(this, R.layout.card_view)
                            .build();

        }


        arFragment.setOnTapArPlaneListener((hitResult, plane, motionEvent) -> {
            Anchor anchor = hitResult.createAnchor();
            AnchorNode anchorNode = new AnchorNode(anchor);
            anchorNode.setParent(arFragment.getArSceneView().getScene());
            Node plcedObject = createViewRenderable();
            anchorNode.addChild(plcedObject);
//            ModelRenderable.builder()
//                    .setSource(this, Uri.parse("model.sfb"))
//                    .build()
//                    .thenAccept(modelRenderable -> addModelToScene(anchor, modelRenderable))
//                    .exceptionally(throwable -> {
//                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                        builder.setMessage(throwable.getMessage())
//                                .show();
//                        return null;
//                    });
        });
        arFragment.getArSceneView().getScene().addOnUpdateListener(this::onUpdate);
    }

//    private void addModelToScene(Anchor anchor, ModelRenderable modelRenderable) {
//        AnchorNode anchorNode = new AnchorNode(anchor);
//        TransformableNode transformableNode = new TransformableNode(arFragment.getTransformationSystem());
//        transformableNode.setParent(anchorNode);
//        transformableNode.setRenderable(modelRenderable);
//        arFragment.getArSceneView().getScene().addChild(anchorNode);
//        transformableNode.select();
//    }

    private Node createViewRenderable() {
        Node base = new Node();

        Node object = new Node();
        object.setParent(base);
        object.setRenderable(objectRenderable.getNow(null));


        infoCard.setParent(object);
        infoCard.setEnabled(false);
        infoCard.setRenderable(infocardRendrable.getNow(null));
        infoCard.setLocalPosition(new Vector3(0.0f, 0.7f, 0.0f));

        object.setOnTapListener((hitTestResult, motionEvent) -> infoCard.setEnabled(!infoCard.isEnabled()));
        return base;
    }

    public void onUpdate(FrameTime frameTime) {
        Frame frame = arFragment.getArSceneView().getArFrame();
        if (frame == null) {
            return;
        }
        if (infoCard == null) {
            return;
        }
        if (arFragment.getArSceneView().getScene() == null) {
            return;
        }
        Vector3 cameraPosition = arFragment.getArSceneView().getScene().getCamera().getWorldPosition();
        Vector3 cardPosition = infoCard.getWorldPosition();
        Vector3 direction = Vector3.subtract(cameraPosition, cardPosition);
        Quaternion lookRotation = Quaternion.lookRotation(direction, Vector3.up());
        infoCard.setWorldRotation(lookRotation);
    }

    public void playAudio(View view) {

    }

}

