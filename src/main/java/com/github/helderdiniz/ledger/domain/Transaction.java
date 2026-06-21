package com.github.helderdiniz.ledger.domain;

import java.time.OffsetDateTime;
import java.util.UUID;

public class Transaction {
    // external identification but also used as ID. in a 'real-setting' we would also have an ID that would serve as the
    // PK (auto-increment, or UUID, depending on requirements)
    private final UUID referenceId;
    private final Money amount;
    private final OffsetDateTime date;
    private final TransactionType type;
    private final String description;
    // For a more "prod-esquee" solution, we should/could have some other auditing fields,
    // such as 'createdAt', 'updatedAt', 'createdBy', etc.

    private Transaction(final Builder builder) {
        referenceId = builder.referenceId;
        amount = builder.amount;
        date = builder.date;
        type = builder.type;
        description = builder.description;
    }

    public static Builder newBuilder(final Transaction copy) {
        final var builder = new Builder();
        builder.referenceId = copy.getReferenceId();
        builder.amount = copy.getAmount();
        builder.date = copy.getDate();
        builder.type = copy.getType();
        builder.description = copy.getDescription();
        return builder;
    }


    @Override
    public String toString() {
        return "Transaction{" +
               "referenceId=" + referenceId +
               ", amount=" + amount +
               ", date=" + date +
               ", type=" + type +
               ", description='" + description + '\'' +
               '}';
    }

    public UUID getReferenceId() {
        return referenceId;
    }

    public Money getAmount() {
        return amount;
    }

    public OffsetDateTime getDate() {
        return date;
    }

    public TransactionType getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public static final class Builder {
        private UUID referenceId;
        private Money amount;
        private OffsetDateTime date;
        private TransactionType type;
        private String description;

        private Builder() {
        }

        public static Builder newBuilder() {
            return new Builder();
        }

        public Builder referenceId(final UUID referenceId) {
            this.referenceId = referenceId;
            return this;
        }

        public Builder amount(final Money amount) {
            this.amount = amount;
            return this;
        }

        public Builder date(final OffsetDateTime date) {
            this.date = date;
            return this;
        }

        public Builder type(final TransactionType type) {
            this.type = type;
            return this;
        }

        public Builder description(final String description) {
            this.description = description;
            return this;
        }

        public Transaction build() {
            return new Transaction(this);
        }
    }
}
