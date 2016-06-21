package pl.themolka.ibot.storage;

import com.mongodb.Block;
import org.bson.Document;

public class DefinedTypeBlock<T> implements Block<Document> {
    private final Block<T> block;
    private final T type;

    public DefinedTypeBlock(Block<T> block, T type) {
        this.block = block;
        this.type = type;
    }

    @Override
    public void apply(Document document) {
        if (this.getBlock() != null && this.getType() != null) {
            ((StorageDocument) this.getType()).putAll(document);
            this.getBlock().apply(this.getType());
        }
    }

    public Block<T> getBlock() {
        return this.block;
    }

    public T getType() {
        return this.type;
    }
}
