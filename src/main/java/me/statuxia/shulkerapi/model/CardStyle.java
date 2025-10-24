package me.statuxia.shulkerapi.model;

import jakarta.persistence.*;

import java.util.Objects;

@Entity(name = "CardStyle")
@Table(name = "card_style")
public class CardStyle {

    @Id
    @Enumerated(EnumType.STRING)
    private CardStyleType type;

    @Column(name = "card_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private CardType cardType;

    @Column(name = "price", nullable = false)
    private Long price;

    public CardStyleType getType() {
        return type;
    }

    public void setType(CardStyleType type) {
        this.type = type;
    }

    public CardType getCardType() {
        return cardType;
    }

    public void setCardType(CardType cardType) {
        this.cardType = cardType;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final CardStyle cardStyle = (CardStyle) o;
        return type == cardStyle.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CardStyle{");
        sb.append("type=").append(type);
        sb.append('}');
        return sb.toString();
    }
}
