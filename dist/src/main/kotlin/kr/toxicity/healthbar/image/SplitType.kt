package kr.toxicity.healthbar.image

import kr.toxicity.healthbar.api.image.NamedProcessedImage
import kr.toxicity.healthbar.api.image.ProcessedImage
import kr.toxicity.healthbar.util.removeEmptyWidth
import java.awt.AlphaComposite
import java.awt.image.BufferedImage

enum class SplitType {
    LEFT {
        override fun split(target: NamedProcessedImage, split: Int): List<NamedProcessedImage> {
            val saveName = target.name.substringBefore('.')
            return (1..split).map {
                NamedProcessedImage(
                    "${saveName}_$it.png",
                    ProcessedImage(
                        target.image.image.getSubimage(0, 0, (it.toDouble() / split * target.image.image.width).toInt().coerceAtLeast(1), target.image.image.height),
                        target.image.xOffset,
                        target.image.yOffset
                    )
                )
            }
        }
    },
    RIGHT {
        override fun split(target: NamedProcessedImage, split: Int): List<NamedProcessedImage> {
            val saveName = target.name.substringBefore('.')
            return (1..split).map {
                val getWidth = (it.toDouble() / split * target.image.image.width).toInt().coerceAtLeast(1)
                val xOffset = target.image.image.width - getWidth
                NamedProcessedImage(
                    "${saveName}_$it.png",
                    ProcessedImage(
                        target.image.image.getSubimage(xOffset, 0, getWidth, target.image.image.height),
                        xOffset + target.image.xOffset,
                        target.image.yOffset
                    )
                )
            }
        }
    },
    UP {
        override fun split(target: NamedProcessedImage, split: Int): List<NamedProcessedImage> {
            val saveName = target.name.substringBefore('.')
            return (1..split).map {
                val getHeight = (it.toDouble() / split * target.image.image.height).toInt().coerceAtLeast(1)
                NamedProcessedImage(
                    "${saveName}_$it.png",
                    BufferedImage(target.image.image.width, target.image.image.height, BufferedImage.TYPE_INT_ARGB).apply {
                        createGraphics().run {
                            composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER)
                            drawImage(target.image.image.getSubimage(0, target.image.image.height - getHeight, target.image.image.width, getHeight), 0, target.image.image.height - getHeight, null)
                            dispose()
                        }
                    }.removeEmptyWidth(target.image.xOffset, target.image.yOffset) ?: throw RuntimeException()
                )
            }
        }
    },
    DOWN {
        override fun split(target: NamedProcessedImage, split: Int): List<NamedProcessedImage> {
            val saveName = target.name.substringBefore('.')
            return (1..split).map {
                NamedProcessedImage(
                    "${saveName}_$it.png",
                    BufferedImage(target.image.image.width, target.image.image.height, BufferedImage.TYPE_INT_ARGB).apply {
                        createGraphics().run {
                            composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER)
                            drawImage(target.image.image.getSubimage(0, 0, target.image.image.width, (it.toDouble() / split * target.image.image.height).toInt().coerceAtLeast(1)), 0, 0, null)
                            dispose()
                        }
                    }.removeEmptyWidth(target.image.xOffset, target.image.yOffset) ?: throw RuntimeException()
                )
            }
        }
    },
    ;
    abstract fun split(target: NamedProcessedImage, split: Int): List<NamedProcessedImage>
}