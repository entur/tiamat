//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.11.05 at 07:41:01 PM CET 
//


package uk.org.netex.netex;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * Type for a step in NAVIGATION PATH.
 * 
 * <p>Java class for PathLinkInSequence_VersionedChildStructure complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PathLinkInSequence_VersionedChildStructure">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.netex.org.uk/netex}LinkInLinkSequence_VersionedChildStructure">
 *       &lt;sequence>
 *         &lt;group ref="{http://www.netex.org.uk/netex}PathLinkInSequenceGroup"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PathLinkInSequence_VersionedChildStructure", propOrder = {
    "pathLinkRef",
    "description",
    "reverse",
    "heading",
    "transition",
    "label",
    "views"
})
@XmlSeeAlso({
    PathLinkInSequence.class
})
public class PathLinkInSequence_VersionedChildStructure
    extends LinkInLinkSequence_VersionedChildStructure
{

    @XmlElement(name = "PathLinkRef", required = true)
    protected PathLinkRefStructure pathLinkRef;
    @XmlElement(name = "Description")
    protected MultilingualString description;
    @XmlElement(name = "Reverse")
    protected Boolean reverse;
    @XmlElement(name = "Heading")
    @XmlSchemaType(name = "NMTOKEN")
    protected PathHeadingEnumeration heading;
    @XmlElement(name = "Transition")
    @XmlSchemaType(name = "NMTOKEN")
    protected TransitionEnumeration transition;
    @XmlElement(name = "Label")
    protected MultilingualString label;
    protected PathLinkInSequence_VersionedChildStructure.Views views;

    /**
     * Gets the value of the pathLinkRef property.
     * 
     * @return
     *     possible object is
     *     {@link PathLinkRefStructure }
     *     
     */
    public PathLinkRefStructure getPathLinkRef() {
        return pathLinkRef;
    }

    /**
     * Sets the value of the pathLinkRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link PathLinkRefStructure }
     *     
     */
    public void setPathLinkRef(PathLinkRefStructure value) {
        this.pathLinkRef = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link MultilingualString }
     *     
     */
    public MultilingualString getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link MultilingualString }
     *     
     */
    public void setDescription(MultilingualString value) {
        this.description = value;
    }

    /**
     * Gets the value of the reverse property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReverse() {
        return reverse;
    }

    /**
     * Sets the value of the reverse property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReverse(Boolean value) {
        this.reverse = value;
    }

    /**
     * Gets the value of the heading property.
     * 
     * @return
     *     possible object is
     *     {@link PathHeadingEnumeration }
     *     
     */
    public PathHeadingEnumeration getHeading() {
        return heading;
    }

    /**
     * Sets the value of the heading property.
     * 
     * @param value
     *     allowed object is
     *     {@link PathHeadingEnumeration }
     *     
     */
    public void setHeading(PathHeadingEnumeration value) {
        this.heading = value;
    }

    /**
     * Gets the value of the transition property.
     * 
     * @return
     *     possible object is
     *     {@link TransitionEnumeration }
     *     
     */
    public TransitionEnumeration getTransition() {
        return transition;
    }

    /**
     * Sets the value of the transition property.
     * 
     * @param value
     *     allowed object is
     *     {@link TransitionEnumeration }
     *     
     */
    public void setTransition(TransitionEnumeration value) {
        this.transition = value;
    }

    /**
     * Gets the value of the label property.
     * 
     * @return
     *     possible object is
     *     {@link MultilingualString }
     *     
     */
    public MultilingualString getLabel() {
        return label;
    }

    /**
     * Sets the value of the label property.
     * 
     * @param value
     *     allowed object is
     *     {@link MultilingualString }
     *     
     */
    public void setLabel(MultilingualString value) {
        this.label = value;
    }

    /**
     * Gets the value of the views property.
     * 
     * @return
     *     possible object is
     *     {@link PathLinkInSequence_VersionedChildStructure.Views }
     *     
     */
    public PathLinkInSequence_VersionedChildStructure.Views getViews() {
        return views;
    }

    /**
     * Sets the value of the views property.
     * 
     * @param value
     *     allowed object is
     *     {@link PathLinkInSequence_VersionedChildStructure.Views }
     *     
     */
    public void setViews(PathLinkInSequence_VersionedChildStructure.Views value) {
        this.views = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element ref="{http://www.netex.org.uk/netex}PathLinkView"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "pathLinkView"
    })
    public static class Views {

        @XmlElement(name = "PathLinkView", required = true)
        protected PathLinkView pathLinkView;

        /**
         * Gets the value of the pathLinkView property.
         * 
         * @return
         *     possible object is
         *     {@link PathLinkView }
         *     
         */
        public PathLinkView getPathLinkView() {
            return pathLinkView;
        }

        /**
         * Sets the value of the pathLinkView property.
         * 
         * @param value
         *     allowed object is
         *     {@link PathLinkView }
         *     
         */
        public void setPathLinkView(PathLinkView value) {
            this.pathLinkView = value;
        }

    }

}
