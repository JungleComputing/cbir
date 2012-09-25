package cbir.gui;

import java.awt.EventQueue;
import java.net.URISyntaxException;

import cbir.MatchTable;
import cbir.backend.MultiArchiveIndex;
import cbir.envi.EnviHeader;
import cbir.envi.FloatImage;
import cbir.envi.ImageIdentifier;
import cbir.envi.PreviewImage;
import cbir.gui.commands.Command;
import cbir.gui.commands.GetHeaderCommand;
import cbir.gui.commands.GetImageCommand;
import cbir.gui.commands.GetIndexCommand;
import cbir.gui.commands.GetPreviewCommand;
import cbir.gui.commands.QueryCommand;
import cbir.node.ControlNode;

public class Controller {

    private final Gui view;
    private final ControlNode searchEngine;

    public Controller() {
        view = new Gui();
        searchEngine = ControlNode.createControlNode();

    }

    public void start() {

        System.out.println("Activating ControlNode");
        searchEngine.activate(this);
        System.out.println("ControlNode started");

        view.setController(this);
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    view.enable();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                searchEngine.done();
            }
        });
    }

    private void submit(Command command) {
        searchEngine.submit(command);
    }

    public void requestImage(ImageIdentifier imageID, String[] stores) {
        submit(new GetImageCommand(imageID, stores));
    }

    public void requestImage(ImageIdentifier imageID) {
        submit(new GetImageCommand(imageID));
    }

    public void requestHeader(ImageIdentifier imageID, String[] stores) {
        submit(new GetHeaderCommand(imageID, stores));
    }

    public void requestHeader(ImageIdentifier imageID) {
        submit(new GetHeaderCommand(imageID));
    }

    public void requestPreviewImage(ImageIdentifier imageID, int red,
            int green, int blue, String... stores) {
        submit(new GetPreviewCommand(imageID, red, green, blue, stores));
    }

    public void requestIndex() {
        submit(new GetIndexCommand());
    }

    public void doQuery(QueryInput qi) {
        if (qi.isLocal()) {
            submit(new QueryCommand(qi.getFloatImage()));
        } else {
            submit(new QueryCommand(qi.getID()));
        }
    }

    public void deliverResult(final MatchTable[] tables) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    view.deliverResult(tables);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void deliverImage(final FloatImage image) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    view.deliverImage(image);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    
    public void deliverHeader(final EnviHeader header) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    view.deliverHeader(header);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void deliverIndex(final MultiArchiveIndex index) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    view.deliverIndex(index);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void deliverPreview(final PreviewImage preview) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    view.deliverPreview(preview);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void main(String[] args) throws URISyntaxException {
        new Controller().start();
    }
}
